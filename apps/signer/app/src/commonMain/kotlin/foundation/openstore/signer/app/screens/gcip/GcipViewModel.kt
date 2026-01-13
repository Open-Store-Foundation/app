package foundation.openstore.signer.app.screens.gcip

import com.openstore.app.core.async.Relay
import com.openstore.app.log.L
import com.openstore.app.mvi.MviFeature
import com.openstore.app.mvi.contract.MviAction
import com.openstore.app.mvi.contract.MviState
import com.openstore.app.mvi.contract.MviViewState
import com.openstore.app.mvi.props.MviProperty
import com.openstore.app.ui.AppTheme
import foundation.openstore.gcip.core.CallerData
import foundation.openstore.gcip.core.CommonResponse
import foundation.openstore.gcip.core.GcipScheme
import foundation.openstore.gcip.core.SignerRequest
import foundation.openstore.gcip.core.transport.GcipStatus
import foundation.openstore.gcip.core.util.GcipErrorContext
import foundation.openstore.gcip.core.util.GcipResult
import foundation.openstore.gcip.core.util.getOrCtx
import foundation.openstore.gcip.platform.GcipDataBundle
import foundation.openstore.gcip.core.handler.GcipSignerHandler
import foundation.openstore.gcip.core.util.GcipOriginComparator
import foundation.openstore.signer.app.data.dao.ConnectionEntity
import foundation.openstore.signer.app.data.dao.LocalCredential
import foundation.openstore.signer.app.data.dao.WalletEntity
import foundation.openstore.signer.app.data.dao.WalletWithConnections
import foundation.openstore.signer.app.data.passcode.SecureStore
import foundation.openstore.signer.app.data.settings.SettingTheme
import foundation.openstore.signer.app.data.settings.WalletSettingsRepository
import foundation.openstore.signer.app.data.wallet.WalletInteractor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

sealed interface GcipEvent {
    class Response(val result: ByteArray) : GcipEvent
    class Error(val result: ByteArray?, val e: Throwable? = null) : GcipEvent
    object Selected : GcipEvent
    object ChangeWallet : GcipEvent
}

sealed interface GcipAction : MviAction {
    data object Disconnect : GcipAction
    data object Init : GcipAction
    data class SelectWallet(val wallet: WalletEntity) : GcipAction
    data object ChangeWallet : GcipAction
    data class Confirm(val store: SecureStore) : GcipAction
}

data class GcipState(
    val isLoading: Boolean = false,
    val request: SignerRequest? = null,
    val wallets: List<WalletWithConnections> = emptyList(),
    val selectedWallet: WalletEntity? = null,
    val connection: ConnectionEntity? = null,
    val credential: LocalCredential? = null,
) : MviState

class GcipViewState(
    val isLoading: MviProperty<Boolean>,
    val wallets: MviProperty<List<WalletWithConnections>>,
    val selectedWallet: MviProperty<WalletEntity?>,
    val connection: MviProperty<ConnectionEntity?>,
    val credential: MviProperty<LocalCredential?>,
    val request: MviProperty<SignerRequest?>,
) : MviViewState

class GcipViewModel(
    private val bundle: GcipDataBundle,
    private val settingsRepository: WalletSettingsRepository,
    private val walletInteractor: WalletInteractor,
    private val comparator: GcipOriginComparator,
    private val signerHandler: GcipSignerHandler,
) : MviFeature<GcipAction, GcipState, GcipViewState>(
    initState = GcipState(),
    initAction = GcipAction.Init
) {

    private val relay = Relay<GcipEvent>()
    val events: Flow<GcipEvent> = relay.events

    suspend fun appTheme(): AppTheme? {
        return when (settingsRepository.getTheme()) {
            SettingTheme.Dark -> AppTheme.Dark
            SettingTheme.Light -> AppTheme.Light
            SettingTheme.System -> null
        }
    }

    override fun createViewState(): GcipViewState {
        return buildViewState {
            GcipViewState(
                isLoading = mviProperty { it.isLoading },
                wallets = mviProperty { it.wallets },
                selectedWallet = mviProperty { it.selectedWallet },
                connection = mviProperty { it.connection },
                credential = mviProperty { it.credential },
                request = mviProperty { it.request },
            )
        }
    }

    override suspend fun executeAction(action: GcipAction) {
        when (action) {
            is GcipAction.Init -> handleInit()
            is GcipAction.Disconnect -> {
                handleError(GcipResult.ctx(GcipStatus.UserCanceled))
            }
            is GcipAction.SelectWallet -> {
                setState { copy(selectedWallet = action.wallet) }
                relay.emit(GcipEvent.Selected)
            }
            is GcipAction.ChangeWallet -> {
                setState { copy(selectedWallet = null) }
                relay.emit(GcipEvent.ChangeWallet)
            }
            is GcipAction.Confirm -> handleConfirm(action.store)
        }
    }

    private suspend fun handleInit() {
        try {
            parseRequest()?.let {
                handleRequest(request = it)
            }
        } catch (e: Exception) {
            L.e(e)
            handleError(GcipResult.ctx(GcipStatus.UnknownError))
            return
        }
    }

    private suspend fun parseRequest(): SignerRequest? {
        val data = bundle.getData() ?: run {
            relay.emit(GcipEvent.Error(null))
            return null
        }

        val request = signerHandler.retrieveRequest(data, bundle.getCaller())
            .getOrCtx { ctx ->
                ctx.e?.printStackTrace()
                handleError(ctx)
                return null
            }

        return request
    }

    private fun handleError(ctx: GcipErrorContext) {
        stateScope.launch {
            val state = obtainState()
            val errorCtx = ctx.withEmptyBlock { state.request?.block?.toGcip() }
            if (errorCtx.block == null) {
                relay.emit(GcipEvent.Error(null))
            } else {
                val bytes = signerHandler.createError(errorCtx)
                relay.emit(GcipEvent.Error(bytes))
            }
        }
    }

    // Spending Groups
    // Create Spending Group
    //
    // Tab1:
    // Add spending
    // List of spending
    //
    // Tab2:
    // Add People by Nicknames
    // List of people
    private suspend fun handleResponse(response: CommonResponse) {
        val data = signerHandler.createResponse(response)
            .getOrCtx { ctx ->
                handleError(ctx)
                return
            }

        relay.emit(GcipEvent.Response(data))
    }

    // TODO move somewhere
    private fun isSameCaller(connection: ConnectionEntity, callerData: CallerData.Raw): Boolean {
        val scheme = connection.callerScheme?.let(GcipScheme::from)
        val initCaller = CallerData.Initial(scheme, connection.callerId, connection.callerSignature)
        if (!comparator.isCallerBelongsTo(initCaller, callerData)) {
            handleError(GcipResult.ctx(GcipStatus.UnknownCaller))
            return false
        }

        return true
    }

    private suspend fun handleRequest(request: SignerRequest) {
        setState { copy(request = request, isLoading = true) }

        try {
            when (request) {
                is SignerRequest.Exchange -> { // TODO handle transport
                    val response = walletInteractor.exchange(request)
                        .getOrCtx { ctx ->
                            handleError(ctx)
                            return
                        }

                    handleResponse(response)
                }

                is SignerRequest.Connect -> {
                    val wallets = walletInteractor.getWallestWithConnections()
                    setState { copy(wallets = wallets, isLoading = false) }
                }

                is SignerRequest.Extend -> {
                    val connection = walletInteractor.findConnectionBy(request.connectionId)
                    if (connection == null) {
                        handleError(GcipResult.ctx(GcipStatus.UnknownConnection))
                        return
                    }

                    if (!isSameCaller(connection = connection, callerData = request.callerData)) {
                        return
                    }

                    val wallet = walletInteractor.findWalletBy(connection.walletId)
                    if (wallet == null) {
                        handleError(GcipResult.ctx(GcipStatus.UnknownConnection))
                        return
                    }

                    setState { copy(selectedWallet = wallet, connection = connection, isLoading = false) }
                }

                is SignerRequest.Sign -> {
                    val connection = walletInteractor.findConnectionBy(request.connectionId)
                    if (connection == null) {
                        handleError(GcipResult.ctx(GcipStatus.UnknownConnection))
                        return
                    }

                    if (!isSameCaller(connection = connection, callerData = request.callerData)) {
                        return
                    }

                    val wallet = walletInteractor.findWalletBy(connection.walletId)
                    if (wallet == null) {
                        handleError(GcipResult.ctx(GcipStatus.UnknownConnection))
                        return
                    }

                    val credential = walletInteractor.findCredentialBy(
                        walletId = wallet.id,
                        credentialId = request.credentialId
                    )

                    if (credential == null) {
                        handleError(GcipResult.ctx(GcipStatus.UnknownCredential))
                        return
                    }

                    setState { copy(selectedWallet = wallet, connection = connection, credential = credential, isLoading = false) }
                }

                is SignerRequest.Disconnect -> {
                    val connection = walletInteractor.findConnectionBy(request.connectionId)

                    if (connection == null) {
                        handleError(GcipResult.ctx(GcipStatus.UnknownConnection))
                        return
                    }

                    if (!isSameCaller(connection = connection, callerData = request.callerData)) {
                        return
                    }

                    setState { copy(connection = connection, isLoading = false) }
                }
            }
        } catch (e: Exception) {
            L.e(e)
            handleError(GcipResult.ctx(GcipStatus.UnknownError))
        }
    }

    private suspend fun handleConfirm(store: SecureStore) {
        val state = obtainState()

        val wallet = state.selectedWallet
        val request = state.request

        if (wallet == null || request == null) {
            handleError(GcipResult.ctx(GcipStatus.UnknownError))
            return
        }

        try {
            setState { copy(isLoading = true) }

            when (request) {
                is SignerRequest.Exchange -> {
                    handleError(GcipResult.ctx(GcipStatus.UnknownError))
                    return
                }

                is SignerRequest.Connect -> {
                    val response = walletInteractor.connect(
                        request = request,
                        wallet = wallet,
                        store = store,
                    ).getOrCtx { ctx ->
                        handleError(ctx)
                        return
                    }

                    handleResponse(response)
                }

                is SignerRequest.Extend -> {
                    val connection = state.connection
                    if (connection == null) {
                        handleError(GcipResult.ctx(GcipStatus.UnknownError))
                        return
                    }

                    val response = walletInteractor.extend(
                        request = request,
                        wallet = wallet,
                        connection = connection,
                        store = store,
                    ).getOrCtx { ctx ->
                        handleError(ctx)
                        return
                    }

                    handleResponse(response)
                }

                is SignerRequest.Sign -> {
                    val connection = state.connection
                    val credential = state.credential

                    if (connection == null || credential == null) {
                        handleError(GcipResult.ctx(GcipStatus.UnknownError))
                        return
                    }

                    val response = walletInteractor.sign(
                        request = request,
                        connection = connection,
                        credential = credential,
                        wallet = wallet,
                        store = store,
                    ).getOrCtx { ctx ->
                        handleError(ctx)
                        return
                    }

                    handleResponse(response)
                }
                is SignerRequest.Disconnect -> {
                    val isDeleted = walletInteractor.disconnect(request.connectionId)
                    if (isDeleted) {
                        val response = CommonResponse.Disconnect(
                            block = request.block,
                            encryption = request.encryption,
                            connectionId = request.connectionId,
                        )

                        handleResponse(response)
                    } else {
                        handleError(GcipResult.ctx(GcipStatus.UnknownConnection))
                    }
                }
            }
        } catch (e: Exception) {
            L.e(e)
            handleError(GcipResult.ctx(GcipStatus.UnknownError))
        } finally {
            setState { copy(isLoading = false) }
        }
    }
}
