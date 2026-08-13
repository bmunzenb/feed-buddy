package com.munzenberger.feed.handler

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertThrows
import org.junit.Test
import java.io.IOException
import java.net.URI
import java.nio.file.Files

class DownloadEnclosuresTest {
    @Test
    fun `it extracts a file for a simple source URL`() {
        val filename = URI("http://www.example.com/foo/bar/file.ext").toURL().filename

        assertEquals("file.ext", filename)
    }

    @Test
    fun `it extracts a file for a source URL with parameters`() {
        val filename = URI("http://www.example.com/foo/bar/file.ext?abc=123").toURL().filename

        assertEquals("file.ext", filename)
    }

    @Test
    fun `it extracts a file for a source URL with encoded characters`() {
        val filename = URI("http://www.example.com/foo/bar/fizz+-%20buzz.ext").toURL().filename

        assertEquals("fizz - buzz.ext", filename)
    }

    @Test
    fun `it maps to a unique local file for a duplicate file name`() {
        val handler = DownloadEnclosures()
        val filename = "duplicate.ext"

        val localFile = handler.targetFileFor(filename)

        if (Files.notExists(localFile)) {
            Files.createFile(localFile)
            localFile.toFile().deleteOnExit()
        }

        val dupFile = handler.targetFileFor(filename)

        assertNotEquals(localFile, dupFile)
    }

    @Test
    fun `it rejects a filename that traverses to the parent directory`() {
        val handler = DownloadEnclosures()

        assertThrows(IOException::class.java) {
            handler.targetFileFor("..")
        }
    }

    @Test
    fun `it rejects a filename that is the current directory`() {
        val handler = DownloadEnclosures()

        assertThrows(IOException::class.java) {
            handler.targetFileFor(".")
        }
    }

    @Test
    fun `it maps to a local file for encoded invalid characters`() {
        val localFile =
            URI(
                "https://anchor.fm/s/10bb8090/podcast/play/10052621/https%3A%2F%2Fd3ctxlq1ktw2nl.cloudfront.net%2Fproduction%2F2020-0-30%2F45841909-22050-1-24041a3ac9d8e.mp3",
            ).toURL().filename

        assertEquals("45841909-22050-1-24041a3ac9d8e.mp3", localFile)
    }

    @Test
    fun `it returns the url unchanged when there are no parameters`() {
        val uri = "http://www.example.com/foo/bar/file.ext".withQueryParameters(emptyMap())

        assertEquals(URI("http://www.example.com/foo/bar/file.ext"), uri)
    }

    @Test
    fun `it appends parameters as a new query string`() {
        val uri = "http://www.example.com/foo/bar/file.ext".withQueryParameters(mapOf("token" to "abc123"))

        assertEquals(URI("http://www.example.com/foo/bar/file.ext?token=abc123"), uri)
    }

    @Test
    fun `it merges parameters into an existing query string`() {
        val uri = "http://www.example.com/foo/bar/file.ext?abc=123".withQueryParameters(mapOf("token" to "abc123"))

        assertEquals(URI("http://www.example.com/foo/bar/file.ext?abc=123&token=abc123"), uri)
    }

    @Test
    fun `it percent encodes parameter names and values`() {
        val uri = "http://www.example.com/foo/bar/file.ext".withQueryParameters(mapOf("a key" to "a&value"))

        assertEquals(URI("http://www.example.com/foo/bar/file.ext?a+key=a%26value"), uri)
    }
}
