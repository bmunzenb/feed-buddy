package com.munzenberger.feed.client

import java.net.URL

class LocationResolver(private val url: URL) {

    fun resolve(location: String?): String? {
        return when {
            location == null ->
                null

            // absolute URL
            location.startsWith("http://") || location.startsWith("https://") ->
                location

            // absolute path
            location.startsWith("/") ->
                url.protocol +
                        "://" +
                        url.authority +
                        location

            // relative path
            else ->
                url.protocol +
                        "://" +
                        url.authority +
                        url.path.substringBeforeLast('/') +
                        "/" +
                        location
        }
    }
}
