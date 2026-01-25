package foundation.openstore.signer.app.screens.pin

import androidx.compose.runtime.Immutable
import com.openstore.app.core.common.fillZeros
import com.openstore.app.core.common.use
import com.openstore.app.log.L
import com.openstore.app.mvi.MviFeature
import com.openstore.app.mvi.MviRelay
import com.openstore.app.mvi.contract.MviAction
import com.openstore.app.mvi.contract.MviState
import com.openstore.app.mvi.contract.MviViewState
import com.openstore.app.mvi.props.MviProperty
import foundation.openstore.signer.app.data.passcode.LockMethod
import foundation.openstore.signer.app.data.passcode.PasscodeRepository
import foundation.openstore.signer.app.data.passcode.PasscodeResult
import foundation.openstore.signer.app.data.passcode.SecureStore
import foundation.openstore.signer.app.data.passcode.SecurityRepository
import foundation.openstore.signer.app.screens.pin.components.PIN_ERROR_DELAY
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Immutable
enum class PinType {
    Create,
    Setup,
    Approve
    ;

    val isCreateOrSetup: Boolean get() = this == Create || this == Setup
}

data class PinState(
    val type: PinType? = null,
    val filled: Int = 0,
    val timeout: Int = 0,
    val hasMemorizedPin: Boolean = false,
    val isSecure: Boolean = true,
    val isBiometrySupported: Boolean = false,
    val isBiometryAvailable: Boolean = false,
    val isRepeat: Boolean = false,
    val isPassNotMatch: Boolean = false,
) : MviState {
    val canShowBiometry get() = isBiometryAvailable && isBiometrySupported && isSecure
    val canUseBiometry get() = canShowBiometry && timeout <= 0
}

data class PinViewState(
    val global: MviProperty<PinState>
) : MviViewState

sealed interface PinPointsEvents {
    data object Success : PinPointsEvents
    data object Created : PinPointsEvents
    data class Input(val index: Int) : PinPointsEvents

    sealed interface Error : PinPointsEvents {
        data object Timeout : Error
        data object Try : Error
        data object Match : Error
    }
}

sealed interface PinEvents {
    data object Cancel : PinEvents
    data class Success(val store: SecureStore?) : PinEvents
    data object Wipe : PinEvents
}

sealed interface PinAction : MviAction {
    data object Init : PinAction
    data class Input(val number: Int) : PinAction
    data object OnResume : PinAction
    data object OnBackSpace : PinAction

    sealed interface ErrorResult : PinAction {
        data object Timeout : ErrorResult
        data object Try : ErrorResult
        data object Match : ErrorResult
    }
}

class PinFeature(
    private val type: PinType,
    private val settings: SecurityRepository,
    private val passcode: PasscodeRepository,
) : MviFeature<PinAction, PinState, PinViewState>(
    initState = PinState(),
    initAction = PinAction.Init,
) {

    private var scheduleTask: Job? = null
    private val channel = MviRelay<PinEvents>()
    val events = channel.events

    private val boxChannel = MviRelay<PinPointsEvents>()
    val boxEvents = boxChannel.events

    private val current = CharArray(PIN_COUNT)
    private val memorisedPin = CharArray(PIN_COUNT)

    override fun createViewState(): PinViewState {
        return buildViewState {
            PinViewState(mviProperty { it })
        }
    }

    override suspend fun executeAction(action: PinAction) {
        when (action) {
            is PinAction.Init -> onInit()
            is PinAction.OnResume -> onResume()
            is PinAction.OnBackSpace -> onBackSpace()
            is PinAction.Input -> onKeyboardNumber(action.number)

            is PinAction.ErrorResult.Match -> reset(isPassNotMatch = true)
            is PinAction.ErrorResult.Timeout -> schedule()
            is PinAction.ErrorResult.Try -> reset()
        }
    }

    private suspend fun getActualType(): PinType {
        return when {
            type.isCreateOrSetup && passcode.has() -> PinType.Approve
            type.isCreateOrSetup -> PinType.Create
            // TODO !hasPasscode -> ERROR
            else -> PinType.Approve
        }
    }

    private suspend fun onInit() {
        val isBioEnabled = false
        val isSecure = passcode.isSecure
        val actualType = getActualType()

        L.d("Passcode onInit: type: $actualType | isBiometryEnabled: $isBioEnabled | isSecure: $isSecure | isAvailable: ${!type.isCreateOrSetup}")
        setState {
            copy(
                type = actualType,
                isBiometrySupported = isBiometrySupported,
                isBiometryAvailable = isBioEnabled, // !type.isCreateOrSetup
                isSecure = isSecure
            )
        }

        if (actualType != PinType.Create) {
            schedule()
        }
    }

    private suspend fun onResume() {
        setState { copy(isSecure = passcode.isSecure) }
        schedule()
    }

    private fun onBackSpace() {
        val state = obtainState()
        if (state.filled == 0) {
            return
        }

        val newFilled = state.filled.dec()
        current[newFilled] = Char.MIN_VALUE

        setState {
            copy(filled = newFilled)
        }
    }

    private suspend fun onKeyboardNumber(num: Int) {
        val state = obtainState()
        val count = state.filled

        if (count == PIN_COUNT) {
            return
        }

        current[count] = ('0' + num)
        setState {
            copy(filled = count + 1, isPassNotMatch = false)
        }

        if (count == PIN_COUNT - 1) {
            onLastItem()
        } else {
            boxChannel.emit(PinPointsEvents.Input(count))
        }
    }

    private suspend fun onLastItem() {
        val state = obtainState()
        when (state.type) {
            PinType.Approve -> {
                val result = current.use { passcode.verify(current) }
                when (result) {
                    is PasscodeResult.Success -> onSuccess(result.store)
                    is PasscodeResult.WrongPin -> boxChannel.emit(PinPointsEvents.Error.Try)
                    is PasscodeResult.Wipe -> channel.emit(PinEvents.Wipe)
                    is PasscodeResult.Locked -> boxChannel.emit(PinPointsEvents.Error.Timeout)
                }
            }
            PinType.Setup,
            PinType.Create -> {
                when {
                    !state.hasMemorizedPin -> {
                        current.copyInto(memorisedPin)
                        current.fillZeros()
                        setState { copy(filled = 0, isRepeat = true, hasMemorizedPin = true) }
                    }
                    else -> {
                        if (current.contentEquals(memorisedPin)) {
                            val store = current.use { passcode.set(it) }

                            clearPinData()
                            setLock(LockMethod.PIN)
                            onSuccess(store)
                        } else {
                            boxChannel.emit(PinPointsEvents.Error.Match)
                        }
                    }
                }
            }
            null -> Unit
        }
    }

    private fun clearPinData() {
        current.fillZeros()
        memorisedPin.fillZeros()
    }

    private suspend fun schedule() {
        val state = obtainState()
        if (state.type == PinType.Create) {
            return
        }

        if (!passcode.isSecure) {
            return
        }

        val diffSec = passcode.timeToUnlock().toInt()
        if (diffSec <= 0) {
            return
        }

        scheduleTask?.cancel()
        scheduleTask = stateScope.launch {
            repeat(diffSec) { num ->
                setState {
                    copy(
                        filled = 0,
                        timeout = diffSec - num
                    )
                }

                delay(1000)
            }

            reset()
        }
    }

    private fun onSuccess(store: SecureStore) {
        bgScope.launch {
            boxChannel.emit(PinPointsEvents.Success)
            delay(PIN_ERROR_DELAY)
            channel.emit(PinEvents.Success(store))
        }
    }

    private suspend fun setLock(type: LockMethod) {
        settings.setLockMethod(type)
    }

    private fun reset(
        isRepeat: Boolean = false,
        isPassNotMatch: Boolean = false,
    ) {
        clearPinData()

        setState {
            copy(
                filled = 0,
                timeout = 0,
                isRepeat = isRepeat,
                isPassNotMatch = isPassNotMatch,
                hasMemorizedPin = false,
            )
        }
    }
}