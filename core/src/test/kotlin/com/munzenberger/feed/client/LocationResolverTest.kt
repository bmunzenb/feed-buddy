package com.munzenberger.feed.client

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.net.URI

class LocationResolverTest {
    @Test
    fun `location resolver handles null location`() {
        val url = URI("https://test.com").toURL()

        val resolved = LocationResolver(url).resolve(null)

        assertNull(resolved)
    }

    @Test
    fun `location resolver handles fully qualified url location`() {
        val url = URI("https://test.com").toURL()

        val resolved = LocationResolver(url).resolve("https://fullyqualified.com")

        assertEquals("https://fullyqualified.com", resolved)
    }

    @Test
    fun `location resolver handles absolute path location`() {
        val url = URI("https://test.com/path/to/resource").toURL()

        val resolved = LocationResolver(url).resolve("/alternate/path")

        assertEquals("https://test.com/alternate/path", resolved)
    }

    @Test
    fun `location resolver handles relative path location`() {
        val url = URI("https://test.com/path/to/resource").toURL()

        val resolved = LocationResolver(url).resolve("alternate/resource")

        assertEquals("https://test.com/path/to/alternate/resource", resolved)
    }

    @Test
    fun `location resolver handles relative path with naked domain`() {
        val url = URI("https://test.com").toURL()

        val resolved = LocationResolver(url).resolve("alternate/resource")

        assertEquals("https://test.com/alternate/resource", resolved)
    }
}
