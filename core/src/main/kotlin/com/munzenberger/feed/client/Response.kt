package com.munzenberger.feed.client

import java.io.InputStream
import java.net.URL

data class Response(
    val resolvedUrl: URL,
    val contentType: ContentType,
    val contentDisposition: ContentDisposition,
    val inStream: InputStream,
) {
    val encoding by lazy { contentType.charset }
}

data class ContentDisposition(
    val value: String?,
) {
    val filename: String?
        // TODO this is a naive parsing strategy that should be replaced with something more robust
        get() =
            value
                ?.split(";")
                ?.map { it.substringBefore('=').trim().lowercase() to it.substringAfter('=').replace("\"", "").trim() }
                ?.firstOrNull { it.first == "filename" }
                ?.second
}

data class ContentType(
    val value: String?,
) {
    val charset: String
        // TODO: better to parse the 'charset' from the content-type
        get() =
            when {
                value == null -> "UTF-8"
                value.contains("UTF-16", true) -> "UTF-16"
                else -> "UTF-8"
            }
}
