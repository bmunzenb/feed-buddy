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
    fun `content type returns UTF-8 charset when no charset parameter is present`() {
        val contentType = ContentType("text/plain")

        assertEquals("UTF-8", contentType.charset)
    }

    @Test
    fun `content type returns UTF-8 charset when present`() {
        val contentType = ContentType("text/plain; charset=utf-8")

        assertEquals("utf-8", contentType.charset)
    }

    @Test
    fun `content type returns UTF-16 charset when present`() {
        val contentType = ContentType("text/plain; charset=utf-16")

        assertEquals("utf-16", contentType.charset)
    }

    @Test
    fun `content type returns charset when value is quoted`() {
        val contentType = ContentType("text/plain; charset=\"utf-8\"")

        assertEquals("utf-8", contentType.charset)
    }

    @Test
    fun `content type returns charset when it is not the first parameter`() {
        val contentType = ContentType("text/xml; boundary=something; charset=utf-8")

        assertEquals("utf-8", contentType.charset)
    }

    @Test
    fun `content type returns charset when attribute name has different case`() {
        val contentType = ContentType("text/plain; CHARSET=utf-8")

        assertEquals("utf-8", contentType.charset)
    }
}
