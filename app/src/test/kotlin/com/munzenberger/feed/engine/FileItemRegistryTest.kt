package com.munzenberger.feed.engine

import com.munzenberger.feed.Item
import org.junit.Assert.assertEquals
import org.junit.Test

class FileItemRegistryTest {

    @Test
    fun `it produces the correct persistable identity with guid`() {

        val item = Item(
                title = "title",
                content = "",
                link = "link",
                guid = "guid",
                timestamp = "",
                enclosures = emptyList()
        )

        assertEquals("guid", item.persistableIdentity)
    }

    @Test
    fun `it produces the correct persistable identity with link`() {

        val item = Item(
                title = "title",
                content = "",
                link = "link",
                guid = "",
                timestamp = "",
                enclosures = emptyList()
        )

        assertEquals("link", item.persistableIdentity)
    }

    @Test
    fun `it produces the correct persistable identity with title`() {

        val item = Item(
                title = "title",
                content = "",
                link = "",
                guid = "",
                timestamp = "",
                enclosures = emptyList()
        )

        assertEquals("title", item.persistableIdentity)
    }

    @Test
    fun `it produces an empty persistable identity with no data`() {

        val item = Item(
                title = "",
                content = "",
                link = "",
                guid = "",
                timestamp = "",
                enclosures = emptyList()
        )

        assertEquals("", item.persistableIdentity)
    }
}
