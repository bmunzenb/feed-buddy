package com.munzenberger.feed

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.time.Instant

class FeedTest {
    @Test
    fun `it can parse a valid timestamp in ISO format`() {
        val timestamp = "2003-12-13T18:30:02Z"
        val instant = Instant.ofEpochSecond(1071340202)

        assertEquals(instant, timestamp.toInstant())
    }

    @Test
    fun `it can parse timestamp in RFC format`() {
        val timestamp = "Thu, 23 Apr 2020 04:01:23 +0000"
        val instant = Instant.ofEpochSecond(1587614483)

        assertEquals(instant, timestamp.toInstant())
    }

    @Test
    fun `it can parse timestamp in modified RFC format`() {
        // TODO fix this:
        // https://stackoverflow.com/questions/45829799/java-time-format-datetimeformatter-rfc-1123-date-time-fails-to-parse-time-zone-n
        val timestamp = "Tue, 18 Mar 2025 19:38:57 PDT"
        val instant = Instant.ofEpochSecond(1742341137)

        assertEquals(instant, timestamp.toInstant())
    }

    @Test
    fun `it does not parse an invalid timestamp format`() {
        val timestamp = ""

        assertNull(timestamp.toInstant())
    }
}
