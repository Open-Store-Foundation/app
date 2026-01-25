package foundation.openstore.signer.app.di

import android.app.Application
import com.openstore.app.core.config.BuildConfig
import com.openstore.app.core.root.DeviceRootProvider
import foundation.openstore.gcip.core.coder.GcipEncryptionCoder
import foundation.openstore.gcip.core.data.GcipDeviceProvider
import foundation.openstore.gcip.core.encryption.HashingProvider
import foundation.openstore.gcip.core.transport.GcipTransformAlgorithm
import foundation.openstore.gcip.core.handler.GcipHandlerFactory
import foundation.openstore.gcip.core.handler.GcipSignerHandler
import foundation.openstore.gcip.core.transport.GcipId
import foundation.openstore.gcip.platform.GcipHandlerFactoryPlatform
import foundation.openstore.gcip.core.util.GcipOriginComparator
import foundation.openstore.gcip.encryption.GcipEncryptionFactory
import foundation.openstore.gcip.platform.data.GcipDeviceProviderPlatform
import foundation.openstore.gcip.transform.GcipTransformer
import foundation.openstore.kitten.api.deps.depLazy
import foundation.openstore.signer.app.data.session.SessionRepository

class GcipComponentDefault(
    private val app: Application,
    private val storageCmp: StorageComponent,
) : GcipComponent {

    override val deviceRootProvider: DeviceRootProvider by depLazy {
        DeviceRootProvider(app)
    }

    override val sessionRepository: SessionRepository by depLazy {
        SessionRepository(storageCmp.appDatabase.exchangeDao())
    }

    override val gcipCoder: GcipEncryptionCoder by depLazy {
        GcipEncryptionFactory.create(sessionProvider)
    }

    private val sessionProvider: GcipEncryptionCoder.Delegate by depLazy {
        object : GcipEncryptionCoder.Delegate {
            override suspend fun getSessionKey(eid: GcipId): ByteArray? {
                return sessionRepository.findSessionEncryptionKey(eid)
            }
        }
    }

    private val deviceProvider: GcipDeviceProvider by depLazy {
        GcipDeviceProviderPlatform()
    }

    override val originComparator: GcipOriginComparator by depLazy {
        GcipOriginComparator()
    }

    override val signerHandler: GcipSignerHandler by depLazy {
        val hasher: HashingProvider = object : HashingProvider {
            override fun sha256(data: ByteArray): ByteArray {
                return GcipTransformer.hash(data, GcipTransformAlgorithm.Sha256)
            }
        }

        GcipHandlerFactory.defaultSignerHandler(
            parti = GcipHandlerFactoryPlatform.platformPariProvider(
                context = app,
                hashing = hasher,
                isEnableOriginValidation = !BuildConfig.isDebug
            ),
            coder = gcipCoder,
            hasher = hasher,
            device = deviceProvider,
        )
    }
}
