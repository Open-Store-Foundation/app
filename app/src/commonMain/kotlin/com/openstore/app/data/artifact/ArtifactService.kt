package com.openstore.app.data.artifact

import com.openstore.app.core.net.json_rpc.util.apiBodyOrError
import com.openstore.app.data.Artifact
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.http.appendPathSegments

interface ArtifactService {
    suspend fun getArtifact(objectId: Long, trackId: Int): Result<Artifact>
}

class ArtifactServiceDefault(
    private val host: String,
    private val client: HttpClient,
) : ArtifactService {
    override suspend fun getArtifact(
        objectId: Long,
        trackId: Int
    ): Result<Artifact> {
        return runCatching {
            val result = client.get(host) {
                url {
                    appendPathSegments("asset/$objectId/$trackId/artifact")
                }
            }

            result.apiBodyOrError()
        }
    }
}