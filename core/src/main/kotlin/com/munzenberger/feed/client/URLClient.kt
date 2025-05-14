package com.munzenberger.feed.client

import java.io.IOException
import java.net.HttpURLConnection
import java.net.URI
import java.net.URL
import java.util.zip.GZIPInputStream
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlin.time.DurationUnit

object URLClientDefaults {
    var userAgent: String = "Feed-Buddy/SNAPSHOT (+https://github.com/bmunzenb/feed-buddy)"
    var timeout: Duration = 30.seconds
    var maxRedirects: Int = 20
}

class URLClient(
    private val userAgent: String = URLClientDefaults.userAgent,
    private val timeout: Duration = URLClientDefaults.timeout,
    private val maxRedirects: Int = URLClientDefaults.maxRedirects,
) {
    companion object {
        @Suppress("ktlint:standard:discouraged-comment-location")
        private val redirectCodes =
            setOf(
                HttpURLConnection.HTTP_MOVED_PERM,
                HttpURLConnection.HTTP_MOVED_TEMP,
                HttpURLConnection.HTTP_SEE_OTHER,
                307, // Temporary Redirect (since HTTP/1.1)
                308, // Permanent Redirect (RFC 7538)
            )
    }

    fun connect(url: URL): Response =
        connect(
            url = url,
            requestProperties =
                mapOf(
                    "User-agent" to userAgent,
                    "Accept" to "application/xml",
                ),
            locations = emptySet(),
        )

    private fun connect(
        url: URL,
        requestProperties: Map<String, String>,
        locations: Set<String>,
    ): Response {
        val connection =
            url.openConnection().apply {
                connectTimeout = timeout.toInt(DurationUnit.MILLISECONDS)
                readTimeout = timeout.toInt(DurationUnit.MILLISECONDS)
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
            inStream = inStream,
        )
    }
}
