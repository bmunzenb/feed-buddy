package com.munzenberger.feed.config

import com.munzenberger.feed.FeedContext
import com.munzenberger.feed.Item
import com.munzenberger.feed.Logger
import com.munzenberger.feed.engine.FeedProcessor
import com.munzenberger.feed.engine.ItemRegistryFactory
import com.munzenberger.feed.filter.ItemFilter
import com.munzenberger.feed.handler.ItemHandler
import com.munzenberger.feed.source.XMLFeedSource
import com.munzenberger.feed.status.FeedStatus
import java.net.URL
import java.util.function.Consumer

class FeedProcessorFactory(
        private val registryFactory: ItemRegistryFactory,
        private val itemFilterFactory: ItemProcessorFactory<ItemFilter> = DefaultItemProcessorFactory(),
        private val itemHandlerFactory: ItemProcessorFactory<ItemHandler> = DefaultItemProcessorFactory(),
        private val statusConsumer: Consumer<FeedStatus>
) {

    fun getInstance(feedConfig: FeedConfig): FeedProcessor {

        val url = URL(feedConfig.url)

        val source = XMLFeedSource(
            source = url,
            userAgent = feedConfig.userAgent
        )

        val itemRegistry = registryFactory.getInstance(url)

        val itemFilter = object : ItemFilter {
            private val filters = feedConfig.filters.map(itemFilterFactory::getInstance)
            override fun evaluate(context: FeedContext, item: Item, logger: Logger): Boolean {
                return filters.all { it.evaluate(context, item, logger) }
            }
        }

        val itemHandler = object : ItemHandler {
            private val handlers = feedConfig.handlers.map(itemHandlerFactory::getInstance)
            override fun execute(context: FeedContext, item: Item, logger: Logger) {
                handlers.forEach { it.execute(context, item, logger) }
            }
        }

        return FeedProcessor(
            source,
            itemRegistry,
            itemFilter,
            itemHandler,
            statusConsumer
        )
    }
}
