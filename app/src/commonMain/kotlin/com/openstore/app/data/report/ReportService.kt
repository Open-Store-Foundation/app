package com.openstore.app.data.report

import com.openstore.app.data.NewReport
import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.appendPathSegments
import io.ktor.http.contentType
import io.ktor.http.isSuccess

interface ReportService {
    suspend fun createReport(newReport: NewReport): Boolean
}

class ReportServiceDefault(
    private val host: String,
    private val client: HttpClient
) : ReportService {
    override suspend fun createReport(newReport: NewReport): Boolean {
        val result = client.post(host) {
            url {
                appendPathSegments("/report/create")
            }
            contentType(ContentType.Application.Json)
            setBody(newReport)
        }

        return result.status.isSuccess()
    }
}
