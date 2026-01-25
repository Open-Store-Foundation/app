package com.openstore.app.data.installation

import com.appmattus.crypto.Algorithm
import com.openstore.app.core.common.toFingerHex
import com.openstore.app.data.Artifact
import com.openstore.app.data.Asset
import com.openstore.app.data.sources.AppChainService
import com.openstore.app.installer.FetchingFailedReason
import com.openstore.app.log.L
import foundation.openstore.core.crypto.SignatureVerifier

sealed interface InstallationValidationResult {
    class Data(
        val fingerprints: List<String>
    ) : InstallationValidationResult

    class Error(
        val reason: FetchingFailedReason
    ) : InstallationValidationResult
}

interface InstallationValidator {
    suspend fun validate(request: FetchingRequest): InstallationValidationResult
}

class InstallationOnChainValidator(
    private val chainCaip2: String,
    private val verifier: SignatureVerifier,
    private val appChainService: AppChainService,
) : InstallationValidator {

    companion object {
        const val WEEK_DELTA = 60 * 60 * 24 * 7
    }

    override suspend fun validate(request: FetchingRequest): InstallationValidationResult {
        val (asset: Asset, artifact: Artifact) = request

        L.d("Validating installation: address=${asset.address}, domain=${asset.domain}, versionCode=${artifact.versionCode}, checksum=${artifact.checksum}, oracleVerified=${asset.isOracleVerified}")

        return runCatching {
            // Checking versions
            if (!asset.isOracleVerified) {
               L.e("Asset is not oracle-verified, skipping on-chain validation")
               return InstallationValidationResult.Data(emptyList())
            }

            val version = appChainService.getVerifiedOwnershipVersion(asset.address, artifact.versionCode)
            if (version == null) {
                L.e("Ownership version not found for address=${asset.address} versionCode=${artifact.versionCode}")
                return InstallationValidationResult.Error(FetchingFailedReason.OwnershipVersionNotFound)
            }
            L.d("Ownership version fetched: version=$version")

            val status = appChainService.getOwnershipVerificationStatus(asset.address, version)
            if (status == null) {
                L.e("Ownership verification status not found for address=${asset.address} version=$version")
                return InstallationValidationResult.Error(FetchingFailedReason.OwnershipVersionNotFound)
            }
            L.d("Ownership verification status: status=${status.status}, lastSuccessDelta=${status.lastSuccessDelta}")

            if (status.status != 1L && status.lastSuccessDelta > WEEK_DELTA) { // TODO Status enum and WEEK_DELTA to config
                L.e("Ownership version is not valid: status=${status.status}, lastSuccessDelta=${status.lastSuccessDelta}, weekDelta=$WEEK_DELTA")
                return InstallationValidationResult.Error(FetchingFailedReason.OwnershipVersionIsNotValid)
            }

            // Checking build info
            val buildInfo = appChainService.getBuildInfo(asset.address, artifact.versionCode)
            if (buildInfo == null) {
                L.e("Build info not found for address=${asset.address} versionCode=${artifact.versionCode}")
                return InstallationValidationResult.Error(FetchingFailedReason.BuildInfoInvalid)
            }
            L.d("Build info fetched: checksum=${buildInfo.checksum}")

            if (buildInfo.checksum != artifact.checksum) {
                L.e("Incorrect checksum: expected=${artifact.checksum}, actual=${buildInfo.checksum}")
                return InstallationValidationResult.Error(FetchingFailedReason.IncorrectChecksum)
            }

            // Checking ownership and proofs
            val ownership = appChainService.getOwnershipInfo(asset = asset.address, version = version)
            if (ownership == null) {
                L.e("Ownership info is null for address=${asset.address} version=$version")
                return InstallationValidationResult.Error(FetchingFailedReason.NetworkError)
            }
            L.d("Ownership info fetched: domain=${ownership.domain}, blockNumber=${ownership.blockNumber}, fingerprints=${ownership.fingerprints.size}")

            if (ownership.domain != asset.website) {
                L.e("Incorrect domain: expected=${asset.website}, actual=${ownership.domain}")
                return InstallationValidationResult.Error(FetchingFailedReason.IncorrectDomain)
            }

            val rawCerts = appChainService.getOwnershipProofsInfo(
                blockNumber = ownership.blockNumber,
                asset = asset.address,
                ownerVersion = version,
            )

            if (rawCerts == null) {
                L.e("Ownership proofs info is null for block=${ownership.blockNumber}, address=${asset.address}, version=$version")
                return InstallationValidationResult.Error(FetchingFailedReason.IncorrectChecksum)
            }
            L.d("Ownership proofs fetched: certs=${rawCerts.certs.size}, proofs=${rawCerts.proofs.size}")

            val fingerprints = ownership.fingerprints
                .map { it.toFingerHex() }
            L.d("Fingerprints prepared: count=${fingerprints.size}")

            val proofs = fingerprints.zip(rawCerts.proofs)
                .toMap()
            L.d("Proofs mapped to fingerprints: count=${proofs.size}")

            val hasher = Algorithm.SHA_256.createDigest()
            val certs = rawCerts.certs
                .map {
                    val data = verifier.decodeCertificate(it).getOrNull()

                    if (data == null) {
                        L.e("Certificate decode failed")
                        return InstallationValidationResult.Error(FetchingFailedReason.CertificateIsNotValid)
                    }

                    data
                }

            val fingersCerts = rawCerts.certs
                .map { hasher.digest(it).toFingerHex() }
                .zip(certs)
            L.d("Prepared certificate fingerprints: count=${fingersCerts.size}")

            for ((finger, cert) in fingersCerts) {
                L.d("Verifying proof for finger=$finger")
                val proof = proofs[finger]
                if (proof == null) {
                    L.e("Proof not found for finger=$finger")
                    return InstallationValidationResult.Error(FetchingFailedReason.ProofIsNotFound)
                }

                val material = prepareMaterial(asset.address, finger)
                val isValid = verifier.verify(cert = cert, data = material, signature = proof)

                if (!isValid) {
                    L.e("Proof is not valid for finger=$finger")
                    return InstallationValidationResult.Error(FetchingFailedReason.ProofIsNotValid)
                }

                L.d("Proof is valid for finger=$finger")
            }

            L.d("Validation succeeded with ${fingerprints.size} fingerprints")
            InstallationValidationResult.Data(fingerprints = fingerprints)
        }.getOrElse {
            L.d("Validation failed with exception: ${it.message}")
            InstallationValidationResult.Error(FetchingFailedReason.NetworkError)
        }
    }

    private fun prepareMaterial(address: String, finger: String): ByteArray {
        val material = "$chainCaip2::${address.lowercase()}::$finger"
        L.d("Material to validate $material")
        return material.toByteArray(Charsets.UTF_8)
    }
}
