package com.openstore.app.data.stat

import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.appendPathSegments
import io.ktor.http.isSuccess

interface StatService {
    suspend fun sendEvent(event: ObjectEventData): Boolean
}

class StatServiceDefault(
    private val host: String,
    private val client: HttpClient
) : StatService {

    override suspend fun sendEvent(event: ObjectEventData): Boolean {
        val result = client.post(host) {
            url {
                appendPathSegments("/event/create")
            }

            setBody(event)
        }

        return result.status.isSuccess()
    }

}