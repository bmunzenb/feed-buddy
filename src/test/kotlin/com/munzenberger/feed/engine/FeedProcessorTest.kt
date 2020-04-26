package com.munzenberger.feed.engine

import com.munzenberger.feed.Feed
import com.munzenberger.feed.Item
import com.munzenberger.feed.handler.ItemHandler
import com.munzenberger.feed.source.FeedSource
import io.mockk.Ordering
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Test
import java.lang.Exception

class FeedProcessorTest {

    @Test
    fun `it gracefully handles exceptions thrown by the feed source`() {

        val mockFeedSource = mockk<FeedSource>().apply {
            every { name } returns "test source"
            every { read() } throws Exception("test exception")
        }

        val processor = FeedProcessor(mockFeedSource, mockk(), mockk())
        processor.execute()
    }

    @Test
    fun `it processes feeds successfully`() {

        val item = Item(
                feedTitle = "test feed",
                title = "item title",
                content = "item content",
                link = "item link",
                guid = "item guid",
                timestamp = "item timestamp",
                enclosures = emptyList()
        )

        val feed = Feed(
                title = "test feed",
                items = listOf(item)
        )

        val mockFeedSource = mockk<FeedSource>().apply {
            every { name } returns "test source"
            every { read() } returns feed
        }

        val mockItemRegistry = mockk<ItemRegistry>().apply {
            every { contains(item) } returns false
            every { add(item) } returns Unit
        }

        val mockItemHandler = mockk<ItemHandler>().apply {
            every { execute(any()) } returns Unit
        }

        val processor = FeedProcessor(mockFeedSource, mockItemRegistry, mockItemHandler)
        processor.execute()

        verify(ordering = Ordering.SEQUENCE) {
            mockFeedSource.name
            mockFeedSource.read()
            mockItemRegistry.contains(item)
            mockItemHandler.execute(item)
            mockItemRegistry.add(item)
        }
    }

    @Test
    fun `it does not process items in the registry`() {

        val item = Item(
                feedTitle = "test feed",
                title = "item title",
                content = "item content",
                link = "item link",
                guid = "item guid",
                timestamp = "item timestamp",
                enclosures = emptyList()
        )

        val feed = Feed(
                title = "test feed",
                items = listOf(item)
        )

        val mockFeedSource = mockk<FeedSource>().apply {
            every { name } returns "test source"
            every { read() } returns feed
        }

        val mockItemRegistry = mockk<ItemRegistry>().apply {
            every { contains(item) } returns true
        }

        val mockItemHandler = mockk<ItemHandler>()

        val processor = FeedProcessor(mockFeedSource, mockItemRegistry, mockItemHandler)
        processor.execute()

        verify(ordering = Ordering.SEQUENCE) {
            mockFeedSource.name
            mockFeedSource.read()
            mockItemRegistry.contains(item)
        }
    }

    @Test
    fun `it does not add failed items to the registry`() {

        val item = Item(
                feedTitle = "test feed",
                title = "item title",
                content = "item content",
                link = "item link",
                guid = "item guid",
                timestamp = "item timestamp",
                enclosures = emptyList()
        )

        val feed = Feed(
                title = "test feed",
                items = listOf(item)
        )

        val mockFeedSource = mockk<FeedSource>().apply {
            every { name } returns "test source"
            every { read() } returns feed
        }

        val mockItemRegistry = mockk<ItemRegistry>().apply {
            every { contains(item) } returns false
        }

        val mockItemHandler = mockk<ItemHandler>().apply {
            every { execute(any()) } throws Exception("text exception")
        }

        val processor = FeedProcessor(mockFeedSource, mockItemRegistry, mockItemHandler)
        processor.execute()

        verify(ordering = Ordering.SEQUENCE) {
            mockFeedSource.name
            mockFeedSource.read()
            mockItemRegistry.contains(item)
            mockItemHandler.execute(item)
        }
    }
}
