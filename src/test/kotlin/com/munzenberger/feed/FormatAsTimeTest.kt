package com.munzenberger.feed

import org.junit.Assert
import org.junit.Test

class FormatAsTimeTest {

    @Test
    fun `it can format milliseconds`() {
        Assert.assertEquals("50 ms", 50L.formatAsTime())
    }

    @Test
    fun `it can format seconds`() {
        Assert.assertEquals("20 sec", 20_100L.formatAsTime())
    }

    @Test
    fun `it can format minutes`() {
        Assert.assertEquals("5 min 20 sec", 320_100L.formatAsTime())
    }
}
