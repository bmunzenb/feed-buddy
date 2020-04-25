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

    @Test
    fun `it maps to a local file for encoded invalid characters`() {

        val handler = DownloadEnclosures()
        val localFile = handler.targetFileFor(URL("https://anchor.fm/s/10bb8090/podcast/play/10052621/https%3A%2F%2Fd3ctxlq1ktw2nl.cloudfront.net%2Fproduction%2F2020-0-30%2F45841909-22050-1-24041a3ac9d8e.mp3"))

        assertEquals(".${File.separator}45841909-22050-1-24041a3ac9d8e.mp3", localFile.path)
    }
}
