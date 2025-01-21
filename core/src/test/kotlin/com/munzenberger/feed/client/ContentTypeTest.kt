package com.munzenberger.feed.client

import org.junit.Assert.assertEquals
import org.junit.Test

class ContentTypeTest {
    @Test
    fun `content type returns UTF-8 charset when empty`() {
        val contentType = ContentType(null)

        assertEquals("UTF-8", contentType.charset)
    }

    @Test
    fun `content type returns UTF-8 charset when present`() {
        val contentType = ContentType("text/plain; charset=utf-8")

        assertEquals("UTF-8", contentType.charset)
    }

    @Test
    fun `content type returns UTF-16 charset when present`() {
        val contentType = ContentType("text/plain; charset=utf-16")

        assertEquals("UTF-16", contentType.charset)
    }
}
