package com.munzenberger.feed.handler

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test
import java.io.File
import java.net.URL

class DownloadEnclosuresTest {

    @Test
    fun `it maps to a local file for a simple source URL`() {

        val handler = DownloadEnclosures()
        val localFile = handler.targetFileFor(URL("http://www.example.com/foo/bar/file.ext"))

        assertEquals(".${File.separator}file.ext", localFile.path)
    }

    @Test
    fun `it maps to a local file for a source URL with parameters`() {

        val handler = DownloadEnclosures()
        val localFile = handler.targetFileFor(URL("http://www.example.com/foo/bar/file.ext?abc=123"))

        assertEquals(".${File.separator}file.ext", localFile.path)
    }

    @Test
    fun `it maps to a local file for a source URL with encoded characters`() {

        val handler = DownloadEnclosures()
        val localFile = handler.targetFileFor(URL("http://www.example.com/foo/bar/fizz+-%20buzz.ext"))

        assertEquals(".${File.separator}fizz - buzz.ext", localFile.path)
    }

    @Test
    fun `it maps to a unique local file for a duplicate source URL`() {

        val handler = DownloadEnclosures()
        val url = URL("http://www.example.com/foo/bar/duplicate.ext")

        val localFile = handler.targetFileFor(url)

        if (localFile.createNewFile()) {
            localFile.deleteOnExit()
        }

        val dupFile = handler.targetFileFor(url)

        assertNotEquals(localFile.path, dupFile.path)
    }
}
