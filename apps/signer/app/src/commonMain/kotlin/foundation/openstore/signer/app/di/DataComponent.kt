package foundation.openstore.signer.app.di

import com.openstore.app.core.root.DeviceRootProvider
import foundation.openstore.kitten.api.Component
import foundation.openstore.signer.app.data.mnemonic.CommonMnemonicWordsRepository
import foundation.openstore.signer.app.data.mnemonic.LocalMnemonicRepository
import foundation.openstore.signer.app.data.mnemonic.MnemonicRepository
import foundation.openstore.signer.app.data.mnemonic.MnemonicWordsRepository
import foundation.openstore.signer.app.data.passcode.PasscodeRepository
import foundation.openstore.signer.app.data.passcode.PasswordStoreImpl
import foundation.openstore.signer.app.data.passcode.SecurityRepository
import foundation.openstore.signer.app.data.passcode.SecurityRepositoryImpl
import foundation.openstore.signer.app.data.settings.WalletSettingsRepository
import foundation.openstore.signer.app.data.settings.WalletSettingsRepositoryImpl
import foundation.openstore.signer.app.data.wallet.PendingWalletRepository
import foundation.openstore.signer.app.data.wallet.WalletInteractor
import foundation.openstore.signer.app.data.wallet.WalletInteractorDefault
import foundation.openstore.signer.app.utils.TimeGenerator
import org.openwallet.kitten.core.depLazy

interface DataComponent : Component {
    val mnemonicRepository: MnemonicRepository
    val passcodeRepository: PasscodeRepository
    val securityRepository: SecurityRepository
    val walletInteractor: WalletInteractor
    val pendingWalletRepository: PendingWalletRepository
    val walletSettingsRepository: WalletSettingsRepository
}

class DataComponentDefault(
    private val storageCmp: StorageComponent,
    private val gcipCmp: GcipComponent,
) : DataComponent {

    private val mnemonicWordsRepository: MnemonicWordsRepository by depLazy {
        CommonMnemonicWordsRepository()
    }

    override val mnemonicRepository: MnemonicRepository by depLazy {
        LocalMnemonicRepository(mnemonicWordsRepository)
    }

    override val walletInteractor: WalletInteractor by depLazy {
        WalletInteractorDefault(
            connectionCredentialDao = storageCmp.appDatabase.connectionCredentialDao(),
            connectionDao = storageCmp.appDatabase.connectionDao(),
            credentialDao = storageCmp.appDatabase.credentialDao(),
            walletDao = storageCmp.appDatabase.walletDao(),
            signingDao = storageCmp.appDatabase.signingDao(),
            exchangerDao = storageCmp.appDatabase.exchangeDao(),
            encryptionCoder = gcipCmp.gcipCoder,
            mnemonicRepository = mnemonicRepository,
        )
    }

    override val pendingWalletRepository: PendingWalletRepository by depLazy {
        PendingWalletRepository()
    }

    override val walletSettingsRepository: WalletSettingsRepository by depLazy {
        WalletSettingsRepositoryImpl(
            keyValueFactory = storageCmp.keyValueFactory
        )
    }

    override val passcodeRepository: PasscodeRepository by depLazy {
        PasscodeRepository(
            passwordStore = PasswordStoreImpl(storageCmp.keyValueFactory.create("pass_store")),
            timer = TimeGenerator(),
            rootProvider = gcipCmp.deviceRootProvider,
        )
    }

    override val securityRepository: SecurityRepository by depLazy {
        SecurityRepositoryImpl(
            keyStore = storageCmp.keyValueFactory.create("security_settings")
        )
    }
}

