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
    fun `it does not parse an invalid timestamp format`() {

        val timestamp = ""

        assertNull(timestamp.toInstant())
    }
}
