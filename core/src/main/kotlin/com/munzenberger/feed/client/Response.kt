package com.munzenberger.feed.client

import java.io.InputStream
import java.net.URL
import java.net.URLDecoder

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
        get() {
            val params = parseHeaderParameters(value)

            // prefer the RFC 5987/6266 extended form (e.g. filename*=UTF-8''caf%C3%A9.txt),
            // which servers use to convey non-ASCII filenames, over the plain filename param
            val extended = params.firstOrNull { it.first == "filename*" }?.second
            if (extended != null) return decodeExtendedValue(extended)

            return params.firstOrNull { it.first == "filename" }?.second
        }
}

data class ContentType(
    val value: String?,
) {
    val charset: String
        get() =
            parseHeaderParameters(value)
                .firstOrNull { it.first == "charset" }
                ?.second
                ?: "UTF-8"
}

// parses a "; key=value" header like Content-Type or Content-Disposition into
// lowercase-keyed attribute/value pairs, respecting semicolons inside quoted values
private fun parseHeaderParameters(value: String?): List<Pair<String, String>> =
    value
        ?.splitRespectingQuotes(';')
        ?.mapNotNull { segment ->
            val parts = segment.split("=", limit = 2).takeIf { it.size == 2 } ?: return@mapNotNull null
            parts[0].trim().lowercase() to parts[1].trim().removeSurrounding("\"")
        }
        ?: emptyList()

private fun String.splitRespectingQuotes(delimiter: Char): List<String> {
    val segments = mutableListOf<String>()
    val current = StringBuilder()
    var inQuotes = false

    for (c in this) {
        when {
            c == '"' -> {
                inQuotes = !inQuotes
                current.append(c)
            }
            c == delimiter && !inQuotes -> {
                segments.add(current.toString())
                current.clear()
            }
            else -> current.append(c)
        }
    }
    segments.add(current.toString())

    return segments
}

// decodes an RFC 5987 extended value: charset''percent-encoded-value
private fun decodeExtendedValue(rawValue: String): String {
    val separatorIndex = rawValue.indexOf("''")
    if (separatorIndex == -1) return rawValue

    val charset = rawValue.substring(0, separatorIndex).ifBlank { "UTF-8" }
    val encoded = rawValue.substring(separatorIndex + 2)

    return runCatching { URLDecoder.decode(encoded, charset) }.getOrDefault(encoded)
}
