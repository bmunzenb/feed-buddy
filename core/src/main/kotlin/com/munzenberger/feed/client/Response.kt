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
        get() =
            value
                ?.split(";")
                ?.drop(1) // skip the "type/subtype" segment
                ?.mapNotNull { param ->
                    val (attribute, attrValue) = param.split("=", limit = 2)
                        .takeIf { it.size == 2 } ?: return@mapNotNull null
                    attribute.trim() to attrValue.trim().removeSurrounding("\"")
                }
                ?.firstOrNull { (attribute, _) -> attribute.equals("charset", ignoreCase = true) }
                ?.second
                ?: "UTF-8"
}
