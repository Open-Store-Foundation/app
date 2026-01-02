package com.openstore.app.data.stat

import kotlinx.serialization.Serializable

@Serializable
enum class InstallationEventType {
    ObjectDownloaded,
    ObjectInstalled,
    ObjectUpdated,
    ObjectDeleted,
}

@Serializable
data class ObjectEventData(
    val eventType: InstallationEventType,

    val objectId: Long,

    val platformId: Int, 
    val objTypeId: Int, 
    val categoryId: Int, 
    val artifactId: String,
    val artifactProtocol: Int, 

    val versionCode: Int?, 
    val versionName: String?, 

    val toVersionCode: Int?, 
    val toVersionName: String? 
)

