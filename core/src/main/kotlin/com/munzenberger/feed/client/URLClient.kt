package com.munzenberger.feed.client

import java.io.IOException
import java.net.HttpURLConnection
import java.net.URI
import java.net.URL
import java.util.zip.GZIPInputStream

object URLClientDefaults {
    var userAgent: String = "Feed-Buddy/SNAPSHOT (+https://github.com/bmunzenb/feed-buddy)"
    var timeout: Int = 30_000 // 30 seconds
    var maxRedirects: Int = 20
}

class URLClient(
    private val userAgent: String? = null,
    private val timeout: Int = URLClientDefaults.timeout,
    private val maxRedirects: Int = URLClientDefaults.maxRedirects
) {
    companion object {

        private val redirectCodes = setOf(
            HttpURLConnection.HTTP_MOVED_PERM,
            HttpURLConnection.HTTP_MOVED_TEMP,
            HttpURLConnection.HTTP_SEE_OTHER,
            307, // Temporary Redirect (since HTTP/1.1)
            308 // Permanent Redirect (RFC 7538)
        )
    }

    fun connect(url: URL): Response {
        val resolvedUserAgent = userAgent ?: URLClientDefaults.userAgent
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

            // HttpURLConnection will not follow redirects if the protocol changes (e.g. HTTP -> HTTPS)
            // so we need to manually handle redirects
            connection.instanceFollowRedirects = false

            val responseCode = connection.responseCode

            // handle redirects
            if (responseCode in redirectCodes) {
                val location = connection.getHeaderField("Location")
                return when (val resolvedLocation: String? = LocationResolver(url).resolve(location)) {
                    null ->
                        throw IOException("Redirect response $responseCode with no 'Location' in header: ${connection.headerFields}")
                    else ->
                        when {
                            locations.contains(resolvedLocation) ->
                                throw IOException("Infinite redirect detected: $resolvedLocation -> $locations")

                            locations.size >= maxRedirects ->
                                throw IOException("Server redirected too many times: ${locations.size}")

                            else -> {
                                connection.disconnect()
                                connect(URI.create(resolvedLocation).toURL(), requestProperties, locations + resolvedLocation)
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
            contentType = ContentType(connection.contentType),
            contentDisposition = ContentDisposition(connection.getHeaderField("Content-Disposition")),
            inStream = inStream
        )
    }
}
