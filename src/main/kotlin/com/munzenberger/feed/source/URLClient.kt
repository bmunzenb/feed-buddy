package com.munzenberger.feed.source

import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.zip.GZIPInputStream

object URLClient {

    data class Response(
            val contentType: String?,
            val inStream: InputStream
    ) {
        val encoding: String
            get() = contentType?.let {
                // TODO: parse the charset from the content type
                when {
                    it.contains("UTF-16", true) -> "UTF-16"
                    else -> null
                }
            } ?: "UTF-8"
    }

    private val redirectCodes = setOf(
            HttpURLConnection.HTTP_MOVED_PERM,
            HttpURLConnection.HTTP_MOVED_TEMP,
            HttpURLConnection.HTTP_SEE_OTHER,
            307, // Temporary Redirect (since HTTP/1.1)
            308) // Permanent Redirect (RFC 7538)

    fun connect(url: URL, userAgent: String? = null, locations: Set<String> = emptySet()): Response {

        val connection = url.openConnection().apply {
            userAgent?.run { setRequestProperty("User-agent", userAgent) }
        }

        if (connection is HttpURLConnection) {
            val responseCode = connection.responseCode
            // handle redirects
            if (responseCode in redirectCodes) {
                return when (val location: String? = connection.getHeaderField("Location")) {
                    null ->
                        throw IOException("Redirect response $responseCode with no 'location' in header: ${connection.headerFields}")
                    else -> {
                        if (locations.contains(location)) {
                            throw IOException("Infinite redirect detected: $location -> $locations")
                        }
                        connect(URL(location), userAgent, locations + location)
                    }
                }
            }
        }

        val contentEncoding: String? = connection.getHeaderField("Content-Encoding")
        val contentType: String? = connection.getHeaderField("Content-Type")
        var inStream = connection.getInputStream()

        // handle gzip encoded responses
        if (contentEncoding.equals("gzip", true)) {
            inStream = GZIPInputStream(inStream)
        }

        return Response(contentType, inStream)
    }
}
