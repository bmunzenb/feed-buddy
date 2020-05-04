package com.munzenberger.feed.handler

import org.junit.Assert.assertEquals
import org.junit.Test

class FormatAsSizeTest {

    @Test
    fun `it can format megabytes`() {
        assertEquals("2.5 MB", 2_621_440L.formatAsSize())
    }
}
