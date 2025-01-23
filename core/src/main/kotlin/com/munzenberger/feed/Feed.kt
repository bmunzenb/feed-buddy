package com.munzenberger.feed

import java.time.Instant
import java.time.format.DateTimeFormatter

data class Feed(
    val title: String,
    val items: List<Item>,
)

data class Item(
    val title: String,
    val content: String,
    val link: String,
    val guid: String,
    val timestamp: String,
    val enclosures: List<Enclosure>,
    val categories: List<String>,
) {
    val timestampAsInstant: Instant? by lazy { timestamp.toInstant() }
}

data class Enclosure(
    val url: String,
)

internal fun String.toInstant(): Instant? {
    try {
        return DateTimeFormatter.ISO_DATE_TIME.parse(this, Instant::from)
    } catch (e: Throwable) {
        // do nothing
    }

    try {
        return DateTimeFormatter.RFC_1123_DATE_TIME.parse(this, Instant::from)
    } catch (e: Throwable) {
        // do nothing
    }

    // could not parse date to Instant :shrug:
    System.err.println("Could not parse timestamp: $this")
    return null
}
