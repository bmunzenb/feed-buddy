package com.munzenberger.feed.source

import com.munzenberger.feed.Enclosure
import com.munzenberger.feed.Feed
import com.munzenberger.feed.Item
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Ignore
import org.junit.Test
import java.net.URI

class XMLFeedSourceTest {
    @Test
    fun `it can parse rss xml`() {
        val url = javaClass.getResource("rss.xml")!!
        val source = XMLFeedSource(url)
        val feed = source.read()

        val expected =
            Feed(
                title = "RSS Title",
                items =
                    listOf(
                        Item(
                            title = "Example entry",
                            content = "Here is some text containing an interesting description.",
                            link = "http://www.example.com/blog/post/1",
                            guid = "7bd204c6-1655-4c27-aeee-53f933c5395f",
                            timestamp = "Sun, 06 Sep 2009 16:20:00 +0000",
                            enclosures = emptyList(),
                            categories = emptyList(),
                        ),
                        Item(
                            title = "Another entry",
                            content = "All good men come to the aid of their country.",
                            link = "http://www.example.com/blog/post/2",
                            guid = "2ea92767-ddf4-4696-a6fd-86d10d156a4c",
                            timestamp = "Wed, 22 Apr 2020 04:01:00 +0000",
                            enclosures =
                                listOf(
                                    Enclosure("http://example.com/file1.mp3"),
                                    Enclosure("http://example.com/file2.mp3"),
                                ),
                            categories =
                                listOf(
                                    "Category A",
                                    "Category B",
                                ),
                        ),
                        Item(
                            title = "Encoded content",
                            content = "This is encoded content.",
                            link = "http://www.example.com/blog/post/3",
                            guid = "11fc01c0-7940-42b6-8bf5-df2730e46dfe",
                            timestamp = "Thu, 23 Apr 2020 04:01:00 +0000",
                            enclosures = emptyList(),
                            categories = emptyList(),
                        ),
                    ),
            )

        assertEquals(expected, feed)
    }

    @Test
    fun `it can parse atom xml`() {
        val url = javaClass.getResource("atom.xml")!!
        val source = XMLFeedSource(url)
        val feed = source.read()

        val expected =
            Feed(
                title = "Example Feed",
                items =
                    listOf(
                        Item(
                            title = "Atom-Powered Robots Run Amok",
                            content = """<div style="info"><p>This is the entry content.</p></div>""",
                            link = "http://example.org/2003/12/13/atom03",
                            guid = "urn:uuid:1225c695-cfb8-4ebb-aaaa-80da344efa6a",
                            timestamp = "2003-12-13T18:30:02Z",
                            enclosures = emptyList(),
                            categories = emptyList(),
                        ),
                        Item(
                            title = "Another entry with enclosures",
                            content = "All good men come to the aid of their country.",
                            link = "http://example.org/2003/12/13/atom03.html",
                            guid = "urn:uuid:7dead42c-7506-4748-ad03-d2d893730975",
                            timestamp = "2020-04-21T04:25:02Z",
                            enclosures =
                                listOf(
                                    Enclosure("http://example.com/file1.mp3"),
                                    Enclosure("http://example.com/file2.mp3"),
                                ),
                            categories =
                                listOf(
                                    "Category A",
                                    "Category B",
                                ),
                        ),
                        Item(
                            title = "HTML entry",
                            content = """<div style="info"><p>This is the entry content.</p></div>""",
                            link = "http://example.org/2003/12/13/atom03.html",
                            guid = "urn:uuid:617ab45c-77c9-4e40-877a-85d062a4ac21",
                            timestamp = "2018-05-05T04:25:02Z",
                            enclosures = emptyList(),
                            categories = emptyList(),
                        ),
                    ),
            )

        assertEquals(expected, feed)
    }

    @Test
    @Ignore("Used to test real feeds.")
    fun `parse real feed`() {
        val url = URI("http://example.com/feed.xml").toURL()
        val source = XMLFeedSource(source = url)
        val feed = source.read()

        assertNotNull(feed)
    }
}
