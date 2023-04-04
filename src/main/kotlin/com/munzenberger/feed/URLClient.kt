package com.munzenberger.feed

import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.zip.GZIPInputStream

typealias ContentDisposition = String
typealias ContentType = String

object URLClient {

    private const val maxRedirects = 20

    var timeout = 30_000 // 30 seconds
    var defaultUserAgent = "Feed-Buddy/SNAPSHOT (+https://github.com/bmunzenb/feed-buddy)"

    data class Response(
            val resolvedUrl: URL,
            val contentType: ContentType?,
            val contentDisposition: ContentDisposition?,
            val inStream: InputStream
    ) {
        val encoding = contentType.charset
    }

    private val redirectCodes = setOf(
            HttpURLConnection.HTTP_MOVED_PERM,
            HttpURLConnection.HTTP_MOVED_TEMP,
            HttpURLConnection.HTTP_SEE_OTHER,
            307, // Temporary Redirect (since HTTP/1.1)
            308) // Permanent Redirect (RFC 7538)

    fun connect(url: URL, userAgent: String? = null): Response {
        val resolvedUserAgent = userAgent ?: defaultUserAgent
        return connect(
            url = url,
            requestProperties = mapOf(
                "User-agent" to resolvedUserAgent,
                "Accept" to "application/xml"
            ),
            locations = emptySet()
        )
    }

    private fun connect(url: URL, requestProperties: Map<String, String>, locations: Set<String>): Response {

        val connection = url.openConnection().apply {
            connectTimeout = timeout
            readTimeout = timeout
        }
        requestProperties.forEach(connection::setRequestProperty)

        if (connection is HttpURLConnection) {
            connection.instanceFollowRedirects = false
            val responseCode = connection.responseCode
            // handle redirects
            if (responseCode in redirectCodes) {
                return when (val location: String? = connection.getHeaderField("Location")) {
                    null ->
                        throw IOException("Redirect response $responseCode with no 'Location' in header: ${connection.headerFields}")
                    else ->
                        when {
                            locations.contains(location) ->
                                throw IOException("Infinite redirect detected: $location -> $locations")
                            locations.size >= maxRedirects ->
                                throw IOException("Server redirected too many times: ${locations.size}")
                            else -> {
                                connection.disconnect()
                                connect(URL(location), requestProperties, locations + location)
                            }
                        }
                }
            }
        }

        var inStream = connection.getInputStream()

        // handle gzip encoded responses
        if (connection.contentEncoding.equals("gzip", true)) {
            inStream = GZIPInputStream(inStream)
        }

        return Response(
                resolvedUrl = url,
                contentType = connection.contentType,
                contentDisposition = connection.getHeaderField("Content-Disposition"),
                inStream = inStream
        )
    }
}

val ContentDisposition?.filename: String?
    // TODO this is a naive parsing strategy that should be replaced with something more robust
    get() = this
            ?.split(";")
            ?.map { it.substringBefore('=').trim().lowercase() to it.substringAfter('=').replace("\"", "").trim() }
            ?.firstOrNull { it.first == "filename" }
            ?.second

internal val ContentType?.charset: String
    // TODO: better to parse the 'charset' from the content-type
    get() = when {
        this == null -> "UTF-8"
        this.contains("UTF-16", true) -> "UTF-16"
        else -> "UTF-8"
    }
