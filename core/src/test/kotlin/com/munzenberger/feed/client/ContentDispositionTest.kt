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

    @Test
    fun `content disposition preserves semicolons inside quoted filenames`() {
        val contentDisposition = ContentDisposition("attachment; filename=\"foo;bar.txt\"")

        assertEquals("foo;bar.txt", contentDisposition.filename)
    }

    @Test
    fun `content disposition decodes an extended filename`() {
        val contentDisposition = ContentDisposition("attachment; filename*=UTF-8''caf%C3%A9.txt")

        assertEquals("café.txt", contentDisposition.filename)
    }

    @Test
    fun `content disposition prefers the extended filename over the plain filename`() {
        val contentDisposition =
            ContentDisposition(
                "attachment; filename=cafe.txt; filename*=UTF-8''caf%C3%A9.txt",
            )

        assertEquals("café.txt", contentDisposition.filename)
    }

    @Test
    fun `content disposition falls back to the raw value for a malformed extended filename`() {
        val contentDisposition = ContentDisposition("attachment; filename*=not-encoded")

        assertEquals("not-encoded", contentDisposition.filename)
    }
}
