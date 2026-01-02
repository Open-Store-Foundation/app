package foundation.openstore.gcip.platform

import kotlin.experimental.ExperimentalObjCName

@OptIn(ExperimentalObjCName::class)
@ObjCName("GcipHandlerFactoryPlatform", exact = true)
actual object GcipHandlerFactoryPlatform : DefaultFactoryPlatform {

    fun extensionHandler(): GcipNsExtensionHandler {
        return GcipNsExtensionHandler()
    }
}
