package com.munzenberger.feed

import com.munzenberger.feed.config.ConfigProvider
import com.munzenberger.feed.config.FeedConfig
import com.munzenberger.feed.config.OperatorConfig
import com.munzenberger.feed.engine.DefaultItemProcessorFactory
import com.munzenberger.feed.engine.ItemRegistry
import com.munzenberger.feed.engine.ItemRegistryFactory
import com.munzenberger.feed.filter.ItemFilter
import com.munzenberger.feed.handler.ItemHandler
import io.mockk.every
import io.mockk.mockk
import org.junit.Ignore
import org.junit.Test
import java.util.function.Consumer

class FeedOperatorTest {
    @Test
    @Ignore("Used to test real configs")
    fun `process real config`() {
        val config =
            OperatorConfig(
                feeds =
                    listOf(
                        FeedConfig(url = "https://www.example.com/feed.xml"),
                    ),
            )

        val configProvider =
            object : ConfigProvider {
                override val name = "FeedOperatorTest"
                override val config = config
                override val timestamp = 0L
            }

        val itemRegistry = mockk<ItemRegistry>()
        every { itemRegistry.contains(any()) } returns false
        every { itemRegistry.add(any()) } returns Unit

        val itemRegistryFactory = mockk<ItemRegistryFactory>()
        every { itemRegistryFactory.getInstance(any()) } returns itemRegistry

        val itemFilterFactory = DefaultItemProcessorFactory<ItemFilter>()

        val itemHandlerFactory = DefaultItemProcessorFactory<ItemHandler>()

        val consumer =
            Consumer<FeedEvent> {
                when (it) {
                    is FeedEvent.ProcessorFeedError -> throw it.error
                    is FeedEvent.ProcessorItemError -> throw it.error
                    is FeedEvent.ProcessorFeedRead -> println(it)
                    else -> Unit
                }
            }

        val operator =
            OnceFeedOperator(
                registryFactory = itemRegistryFactory,
                configProvider = configProvider,
                filterFactory = itemFilterFactory,
                handlerFactory = itemHandlerFactory,
                statusConsumer = consumer,
            )

        operator.start()
    }
}
