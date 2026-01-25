package com.openstore.app.data.review

import com.openstore.app.data.NewReview
import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.appendPathSegments
import io.ktor.http.isSuccess

interface ReviewService {
    suspend fun createReview(newReview: NewReview): Boolean
}

class ReviewServiceDefault(
    private val host: String,
    private val client: HttpClient
) : ReviewService {
    override suspend fun createReview(newReview: NewReview): Boolean {
        val result = client.post(host) {
            url {
                appendPathSegments("/review/create")
            }

            setBody(newReview)
        }

        return result.status.isSuccess()
    }
}
