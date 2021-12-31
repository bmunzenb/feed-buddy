package com.munzenberger.feed

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class URLClientTest {

    @Test
    fun `content disposition returns null for no filename`() {

        val contentDisposition: ContentDisposition = ""

        assertNull(contentDisposition.filename)
    }

    @Test
    fun `content disposition returns filename`() {

        val contentDisposition: ContentDisposition = "attachment; filename=foobar.txt"

        assertEquals("foobar.txt", contentDisposition.filename)
    }

    @Test
    fun `content disposition removes whitespace from filenames`() {

        val contentDisposition: ContentDisposition = "attachment ; filename = foobar.txt "

        assertEquals("foobar.txt", contentDisposition.filename)
    }

    @Test
    fun `content disposition removes quotes from filenames`() {

        val contentDisposition: ContentDisposition = "attachment; filename=\"foobar.txt\""

        assertEquals("foobar.txt", contentDisposition.filename)
    }

    @Test
    fun `content type returns UTF-8 charset when empty`() {

        val contentType: ContentType? = null

        assertEquals("UTF-8", contentType.charset)
    }

    @Test
    fun `content type returns UTF-8 charset when present`() {

        val contentType: ContentType = "text/plain; charset=utf-8"

        assertEquals("UTF-8", contentType.charset)
    }

    @Test
    fun `content type returns UTF-16 charset when present`() {

        val contentType: ContentType = "text/plain; charset=utf-16"

        assertEquals("UTF-16", contentType.charset)
    }
}
