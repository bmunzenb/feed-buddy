package com.munzenberger.feed.client

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ContentDispositionTest {
    @Test
    fun `content disposition returns null for no filename`() {
        val contentDisposition = ContentDisposition("")

        assertNull(contentDisposition.filename)
    }

    @Test
    fun `content disposition returns filename`() {
        val contentDisposition = ContentDisposition("attachment; filename=foobar.txt")

        assertEquals("foobar.txt", contentDisposition.filename)
    }

    @Test
    fun `content disposition removes whitespace from filenames`() {
        val contentDisposition = ContentDisposition("attachment ; filename = foobar.txt ")

        assertEquals("foobar.txt", contentDisposition.filename)
    }

    @Test
    fun `content disposition removes quotes from filenames`() {
        val contentDisposition = ContentDisposition("attachment; filename=\"foobar.txt\"")

        assertEquals("foobar.txt", contentDisposition.filename)
    }
}
