package com.munzenberger.feed.engine

import com.munzenberger.feed.Feed
import com.munzenberger.feed.FeedContext
import com.munzenberger.feed.Item
import com.munzenberger.feed.filter.ItemFilter
import com.munzenberger.feed.handler.ItemHandler
import com.munzenberger.feed.source.FeedSource
import com.munzenberger.feed.source.XMLFeedSource
import com.munzenberger.feed.status.FeedStatus
import io.mockk.Ordering
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Ignore
import org.junit.Test
import java.net.URI
import java.util.function.Consumer

class FeedProcessorTest {
    private val consumer = Consumer<FeedStatus> { }

    private val item =
        Item(
            title = "item title",
            content = "item content",
            link = "item link",
            guid = "item guid",
            timestamp = "item timestamp",
            enclosures = emptyList(),
            categories = emptyList(),
        )

    private val feed =
        Feed(
            title = "test feed",
            items = listOf(item),
        )

    private val source =
        object : FeedSource {
            override val name = "test source"

            override fun read() = feed
        }

    private val context = FeedContext("test source", feed.title)

    @Test
    fun `it gracefully handles exceptions thrown by the feed source`() {
        val mockFeedSource =
            mockk<FeedSource>().apply {
                every { name } returns "test source"
                every { read() } throws Exception("test exception")
            }

        val processor =
            FeedProcessor(
                source = mockFeedSource,
                itemRegistry = mockk(),
                itemFilter = mockk(),
                itemHandler = mockk(),
                statusConsumer = consumer,
            )

        processor.run()
    }

    @Test
    fun `it processes feeds successfully`() {
        val mockItemRegistry =
            mockk<ItemRegistry>().apply {
                every { contains(item) } returns false
                every { add(item) } returns Unit
            }

        val mockItemFilter =
            mockk<ItemFilter>().apply {
                every { evaluate(any(), any(), any()) } returns true
            }

        val mockItemHandler =
            mockk<ItemHandler>().apply {
                every { execute(any(), any(), any()) } returns Unit
            }

        val processor =
            FeedProcessor(
                source = source,
                itemRegistry = mockItemRegistry,
                itemFilter = mockItemFilter,
                itemHandler = mockItemHandler,
                statusConsumer = consumer,
            )

        processor.run()

        verify(ordering = Ordering.SEQUENCE) {
            mockItemRegistry.contains(item)
            mockItemFilter.evaluate(context, item, any())
            mockItemHandler.execute(context, item, any())
            mockItemRegistry.add(item)
        }
    }

    @Test
    fun `it does not process items in the registry`() {
        val mockItemRegistry =
            mockk<ItemRegistry>().apply {
                every { contains(item) } returns true
            }

        val processor =
            FeedProcessor(
                source = source,
                itemRegistry = mockItemRegistry,
                itemFilter = mockk(),
                itemHandler = mockk(),
                statusConsumer = consumer,
            )

        processor.run()

        verify(ordering = Ordering.SEQUENCE) {
            mockItemRegistry.contains(item)
        }
    }

    @Test
    fun `it does not add failed items to the registry`() {
        val mockItemRegistry =
            mockk<ItemRegistry>().apply {
                every { contains(item) } returns false
            }

        val mockItemFilter =
            mockk<ItemFilter>().apply {
                every { evaluate(any(), any(), any()) } returns true
            }

        val mockItemHandler =
            mockk<ItemHandler>().apply {
                every { execute(any(), any(), any()) } throws Exception("test exception")
            }

        val processor =
            FeedProcessor(
                source = source,
                itemRegistry = mockItemRegistry,
                itemFilter = mockItemFilter,
                itemHandler = mockItemHandler,
                statusConsumer = consumer,
            )

        processor.run()

        verify(ordering = Ordering.SEQUENCE) {
            mockItemRegistry.contains(item)
            mockItemHandler.execute(context, item, any())
        }
    }

    @Test
    fun `it does not add execute handlers for filtered items`() {
        val mockItemRegistry =
            mockk<ItemRegistry>().apply {
                every { contains(item) } returns false
            }

        val mockItemFilter =
            mockk<ItemFilter>().apply {
                every { evaluate(any(), any(), any()) } returns false
            }

        val processor =
            FeedProcessor(
                source = source,
                itemRegistry = mockItemRegistry,
                itemFilter = mockItemFilter,
                itemHandler = mockk(),
                statusConsumer = consumer,
            )

        processor.run()

        verify(ordering = Ordering.SEQUENCE) {
            mockItemRegistry.contains(item)
            mockItemFilter.evaluate(context, item, any())
        }
    }

    @Test
    @Ignore("Used to test real feeds.")
    fun `process real feed`() {
        val url = URI("http://example.com/feed.xml").toURL()

        val itemRegistry = mockk<ItemRegistry>()
        every { itemRegistry.contains(any()) } returns false
        every { itemRegistry.add(any()) } returns Unit

        val itemFilter = ItemFilter { _, _, _ -> true }

        val itemHandler = ItemHandler { _, _, _ -> }

        val consumer =
            Consumer<FeedStatus> {
                when (it) {
                    is FeedStatus.ProcessorFeedError -> throw it.error
                    is FeedStatus.ProcessorFeedRead -> println(it)
                    else -> Unit
                }
            }

        val processor =
            FeedProcessor(
                source = XMLFeedSource(url),
                itemRegistry = itemRegistry,
                itemFilter = itemFilter,
                itemHandler = itemHandler,
                statusConsumer = consumer,
            )

        processor.run()
    }
}
