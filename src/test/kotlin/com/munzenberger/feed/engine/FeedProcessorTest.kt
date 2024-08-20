package com.munzenberger.feed.engine

import com.munzenberger.feed.Feed
import com.munzenberger.feed.FeedContext
import com.munzenberger.feed.Item
import com.munzenberger.feed.filter.ItemFilter
import com.munzenberger.feed.handler.ItemHandler
import com.munzenberger.feed.source.FeedSource
import com.munzenberger.feed.status.FeedStatus
import io.mockk.Ordering
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Test
import java.util.function.Consumer

class FeedProcessorTest {

    private val consumer = Consumer<FeedStatus> { }

    private val item = Item(
            title = "item title",
            content = "item content",
            link = "item link",
            guid = "item guid",
            timestamp = "item timestamp",
            enclosures = emptyList()
    )

    private val feed = Feed(
            title = "test feed",
            items = listOf(item)
    )

    private val source = object : FeedSource {
        override val name = "test source"
        override fun read() = feed
    }

    private val context = FeedContext("test source", feed.title)

    @Test
    fun `it gracefully handles exceptions thrown by the feed source`() {

        val mockFeedSource = mockk<FeedSource>().apply {
            every { name } returns "test source"
            every { read() } throws Exception("test exception")
        }

        val processor = FeedProcessor(
                source = mockFeedSource,
                itemRegistry = mockk(),
                itemFilter = mockk(),
                itemHandler = mockk(),
                statusConsumer = consumer
        )

        processor.run()
    }

    @Test
    fun `it processes feeds successfully`() {

        val mockItemRegistry = mockk<ItemRegistry>().apply {
            every { contains(item) } returns false
            every { add(item) } returns Unit
        }

        val mockItemFilter = mockk<ItemFilter>().apply {
            every { evaluate(any(), any(), any()) } returns true
        }

        val mockItemHandler = mockk<ItemHandler>().apply {
            every { execute(any(), any(), any()) } returns Unit
        }

        val processor = FeedProcessor(
            source = source,
            itemRegistry = mockItemRegistry,
            itemFilter = mockItemFilter,
            itemHandler = mockItemHandler,
            statusConsumer = consumer
        )

        processor.run()

        verify(ordering = Ordering.SEQUENCE) {
            mockItemRegistry.contains(item)
            mockItemFilter.evaluate(context, item, consumer)
            mockItemHandler.execute(context, item, consumer)
            mockItemRegistry.add(item)
        }
    }

    @Test
    fun `it does not process items in the registry`() {

        val mockItemRegistry = mockk<ItemRegistry>().apply {
            every { contains(item) } returns true
        }

        val processor = FeedProcessor(
            source = source,
            itemRegistry = mockItemRegistry,
            itemFilter = mockk(),
            itemHandler = mockk(),
            statusConsumer = consumer
        )

        processor.run()

        verify(ordering = Ordering.SEQUENCE) {
            mockItemRegistry.contains(item)
        }
    }

    @Test
    fun `it does not add failed items to the registry`() {

        val mockItemRegistry = mockk<ItemRegistry>().apply {
            every { contains(item) } returns false
        }

        val mockItemFilter = mockk<ItemFilter>().apply {
            every { evaluate(any(), any(), any()) } returns true
        }

        val mockItemHandler = mockk<ItemHandler>().apply {
            every { execute(any(), any(), any()) } throws Exception("test exception")
        }

        val processor = FeedProcessor(
            source = source,
            itemRegistry = mockItemRegistry,
            itemFilter = mockItemFilter,
            itemHandler = mockItemHandler,
            statusConsumer = consumer
        )

        processor.run()

        verify(ordering = Ordering.SEQUENCE) {
            mockItemRegistry.contains(item)
            mockItemHandler.execute(context, item, consumer)
        }
    }

    @Test
    fun `it does not add execute handlers for filtered items`() {

        val mockItemRegistry = mockk<ItemRegistry>().apply {
            every { contains(item) } returns false
        }

        val mockItemFilter = mockk<ItemFilter>().apply {
            every { evaluate(any(), any(), any()) } returns false
        }

        val processor = FeedProcessor(
            source = source,
            itemRegistry = mockItemRegistry,
            itemFilter = mockItemFilter,
            itemHandler = mockk(),
            statusConsumer = consumer
        )

        processor.run()

        verify(ordering = Ordering.SEQUENCE) {
            mockItemRegistry.contains(item)
            mockItemFilter.evaluate(context, item, consumer)
        }
    }
}
