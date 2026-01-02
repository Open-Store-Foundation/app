package foundation.openstore.signer.app.data.adapters

import app.cash.sqldelight.ColumnAdapter
import foundation.openstore.gcip.core.Algorithm
import foundation.openstore.gcip.core.transport.GcipBinaryFormat
import foundation.openstore.gcip.core.transport.GcipCredentialType
import foundation.openstore.gcip.core.transport.GcipTransformAlgorithm
import foundation.openstore.gcip.core.transport.GcipTransportType
import foundation.openstore.gcip.core.transport.GcipId
import foundation.openstore.signer.app.data.dao.SigningTarget
import kotlinx.datetime.Instant
import kotlinx.serialization.json.Json

val InstantAdapter = object : ColumnAdapter<Instant, Long> {
    override fun decode(databaseValue: Long): Instant = Instant.fromEpochMilliseconds(databaseValue)
    override fun encode(value: Instant): Long = value.toEpochMilliseconds()
}

val GcipIdAdapter = object : ColumnAdapter<GcipId, String> {
    override fun decode(databaseValue: String): GcipId = GcipId.from(databaseValue)
    override fun encode(value: GcipId): String = value.fmt
}

val AlgorithmAdapter = object : ColumnAdapter<Algorithm, String> {
    override fun decode(databaseValue: String): Algorithm = Algorithm.valueOf(databaseValue)
    override fun encode(value: Algorithm): String = value.name
}

val GcipTransportTypeAdapter = object : ColumnAdapter<GcipTransportType, Long> {
    override fun decode(databaseValue: Long): GcipTransportType {
        return GcipTransportType.from(databaseValue.toByte())
            ?: throw IllegalArgumentException("Unknown transport type: $databaseValue")
    }

    override fun encode(value: GcipTransportType): Long {
       return value.value.toLong()
    }
}

val CredentialTypeAdapter = object : ColumnAdapter<GcipCredentialType, String> {
    override fun decode(databaseValue: String): GcipCredentialType = GcipCredentialType.valueOf(databaseValue)
    override fun encode(value: GcipCredentialType): String = value.name
}

val BinaryFormatAdapter = object : ColumnAdapter<GcipBinaryFormat, String> {
    override fun decode(databaseValue: String): GcipBinaryFormat = GcipBinaryFormat.valueOf(databaseValue)
    override fun encode(value: GcipBinaryFormat): String = value.name
}

val TransformAlgorithmListAdapter = object : ColumnAdapter<List<GcipTransformAlgorithm>, String> {
    override fun decode(databaseValue: String): List<GcipTransformAlgorithm> =
        if (databaseValue.isEmpty()) emptyList() else Json.decodeFromString(databaseValue)
    override fun encode(value: List<GcipTransformAlgorithm>): String =
        Json.encodeToString(value)
}

val SigningTargetAdapter = object : ColumnAdapter<SigningTarget, Long> {
    override fun decode(databaseValue: Long): SigningTarget = 
        SigningTarget.from(databaseValue.toInt()) ?: SigningTarget.Default
    override fun encode(value: SigningTarget): Long = value.code.toLong()
}
