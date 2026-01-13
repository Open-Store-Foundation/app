package foundation.openstore.signer.app.di

import foundation.openstore.gcip.core.Derivation
import foundation.openstore.gcip.core.transport.GcipId
import foundation.openstore.kitten.api.Component
import foundation.openstore.signer.app.Router
import foundation.openstore.signer.app.screens.AppViewModel
import foundation.openstore.signer.app.screens.connections.ConnectionsFeature
import foundation.openstore.signer.app.screens.create.CreateWalletFeature
import foundation.openstore.signer.app.screens.details.WalletDetailsFeature
import foundation.openstore.signer.app.screens.home.HomeFeature
import foundation.openstore.signer.app.screens.import.ImportWalletFeature
import foundation.openstore.signer.app.screens.list.WalletListFeature
import foundation.openstore.signer.app.screens.mnemonic.verify.VerifyMnemonicFeature
import foundation.openstore.signer.app.screens.mnemonic.viewer.MnemonicViewerFeature
import foundation.openstore.signer.app.screens.mnemonic.viewer.MnemonicViewerMode
import foundation.openstore.signer.app.screens.pin.PinFeature
import foundation.openstore.signer.app.screens.pin.PinType
import foundation.openstore.signer.app.screens.settings.SettingsFeature
import foundation.openstore.signer.app.screens.transactions.TransactionsFeature

interface SignerComponent : Component {
    fun provideMainViewModel(): AppViewModel

    fun provideCreateWalletFeature(): CreateWalletFeature
    fun provideWalletDetailsFeature(walletId: GcipId): WalletDetailsFeature
    fun provideConnectionsFeature(walletId: GcipId): ConnectionsFeature
    fun provideTransactionsFeature(walletId: GcipId): TransactionsFeature
    fun provideHomeFeature(): HomeFeature
    fun providePinFeature(type: PinType): PinFeature
    fun provideWalletListFeature(): WalletListFeature
    fun provideWalletSelectorFeature(types: List<Derivation>): WalletListFeature
    fun provideSettingsFeature(): SettingsFeature
    fun provideVerifyMnemonicFeature(pendingId: String): VerifyMnemonicFeature
    fun provideMnemonicViewerFeature(mode: MnemonicViewerMode): MnemonicViewerFeature
    fun provideImportWalletFeature(): ImportWalletFeature
}

class SignerComponentDefault(
    private val dataCmp: DataComponent,
    private val gcipCmp: GcipComponent,
) : SignerComponent {

    override fun provideMainViewModel(): AppViewModel {
        return AppViewModel(
            settingsRepo = dataCmp.walletSettingsRepository,
            walletInteractor = dataCmp.walletInteractor
        )
    }

    override fun provideCreateWalletFeature(): CreateWalletFeature {
        return CreateWalletFeature()
    }

    override fun provideWalletDetailsFeature(walletId: GcipId): WalletDetailsFeature {
        return WalletDetailsFeature(
            walletId = walletId,
            walletInteractor = dataCmp.walletInteractor,
        )
    }

    override fun provideConnectionsFeature(walletId: GcipId): ConnectionsFeature {
        return ConnectionsFeature(
            walletId = walletId,
            walletInteractor = dataCmp.walletInteractor,
        )
    }

    override fun provideTransactionsFeature(walletId: GcipId): TransactionsFeature {
        return TransactionsFeature(
            walletId = walletId,
            walletInteractor = dataCmp.walletInteractor,
        )
    }

    override fun provideHomeFeature(): HomeFeature {
        return HomeFeature()
    }

    override fun providePinFeature(type: PinType): PinFeature {
        return PinFeature(
            type = type,
            settings = dataCmp.securityRepository,
            passcode = dataCmp.passcodeRepository
        )
    }

    override fun provideWalletListFeature(): WalletListFeature {
        return WalletListFeature(
            walletInteractor = dataCmp.walletInteractor,
            server = gcipCmp.blePeripheralProvider,
        )
    }

    override fun provideWalletSelectorFeature(types: List<Derivation>): WalletListFeature {
        return WalletListFeature(
            walletInteractor = dataCmp.walletInteractor,
            server = gcipCmp.blePeripheralProvider,
        )
    }

    override fun provideSettingsFeature(): SettingsFeature {
        return SettingsFeature(
            settingsRepo = dataCmp.walletSettingsRepository
        )
    }

    override fun provideVerifyMnemonicFeature(pendingId: String): VerifyMnemonicFeature {
        return VerifyMnemonicFeature(
            pendingId = pendingId,
            walletInteractor = dataCmp.walletInteractor,
            pendingWalletRepository = dataCmp.pendingWalletRepository,
            passcodeRepository = dataCmp.passcodeRepository
        )
    }

    override fun provideMnemonicViewerFeature(mode: MnemonicViewerMode): MnemonicViewerFeature {
        return MnemonicViewerFeature(
            mode = mode,
            walletInteractor = dataCmp.walletInteractor,
            pendingWalletRepository = dataCmp.pendingWalletRepository
        )
    }

    override fun provideImportWalletFeature(): ImportWalletFeature {
        return ImportWalletFeature(
            walletInteractor = dataCmp.walletInteractor,
            passcodeRepository = dataCmp.passcodeRepository,
            mnemonicRepository = dataCmp.mnemonicRepository,
        )
    }
}