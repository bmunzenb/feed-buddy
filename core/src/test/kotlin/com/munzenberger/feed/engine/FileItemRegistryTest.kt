package com.munzenberger.feed.engine

import com.munzenberger.feed.Item
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.IOException

class FileItemRegistryTest {
    @get:Rule
    val tempFolder = TemporaryFolder()

    private val item =
        Item(
            title = "title",
            content = "",
            link = "link",
            guid = "guid",
            timestamp = "",
            enclosures = emptyList(),
            categories = emptyList(),
        )

    @Test
    fun `it marks an item as contained after a successful add`() {
        val path = tempFolder.newFile("registry.processed").toPath()
        val registry = FileItemRegistry(path)

        registry.add(item)

        assertTrue(registry.contains(item))
    }

    @Test
    fun `it does not mark an item as contained when the disk write fails`() {
        // resolving the registry file under a nonexistent parent directory
        // forces Files.write to throw instead of succeeding
        val path = tempFolder.root.toPath().resolve("missing-dir").resolve("registry.processed")
        val registry = FileItemRegistry(path)

        assertThrows(IOException::class.java) { registry.add(item) }

        assertFalse(registry.contains(item))
    }
    @Test
    fun `it produces the correct persistable identity with guid`() {
        val item =
            Item(
                title = "title",
                content = "",
                link = "link",
                guid = "guid",
                timestamp = "",
                enclosures = emptyList(),
                categories = emptyList(),
            )

        assertEquals("guid", item.persistableIdentity)
    }

    @Test
    fun `it produces the correct persistable identity with link`() {
        val item =
            Item(
                title = "title",
                content = "",
                link = "link",
                guid = "",
                timestamp = "",
                enclosures = emptyList(),
                categories = emptyList(),
            )

        assertEquals("link", item.persistableIdentity)
    }

    @Test
    fun `it produces the correct persistable identity with title`() {
        val item =
            Item(
                title = "title",
                content = "",
                link = "",
                guid = "",
                timestamp = "",
                enclosures = emptyList(),
                categories = emptyList(),
            )

        assertEquals("title", item.persistableIdentity)
    }

    @Test
    fun `it produces an empty persistable identity with no data`() {
        val item =
            Item(
                title = "",
                content = "",
                link = "",
                guid = "",
                timestamp = "",
                enclosures = emptyList(),
                categories = emptyList(),
            )

        assertEquals("", item.persistableIdentity)
    }
}
