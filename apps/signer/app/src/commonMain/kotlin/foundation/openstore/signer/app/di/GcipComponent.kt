package foundation.openstore.signer.app.di

import com.openstore.app.core.root.DeviceRootProvider
import foundation.openstore.gcip.core.coder.GcipEncryptionCoder
import foundation.openstore.gcip.core.handler.GcipSignerHandler
import foundation.openstore.gcip.core.util.GcipOriginComparator
import foundation.openstore.kitten.api.Component
import foundation.openstore.signer.app.data.session.SessionRepository

interface GcipComponent : Component {
    val deviceRootProvider: DeviceRootProvider
    val signerHandler: GcipSignerHandler
    val originComparator: GcipOriginComparator
    val sessionRepository: SessionRepository
    val gcipCoder: GcipEncryptionCoder
}
