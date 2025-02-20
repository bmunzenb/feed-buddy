package com.munzenberger.feed.engine

import com.munzenberger.feed.FeedEvent
import com.munzenberger.feed.config.FeedConfig
import com.munzenberger.feed.filter.CompositeItemFilter
import com.munzenberger.feed.filter.ItemFilter
import com.munzenberger.feed.handler.CompositeItemHandler
import com.munzenberger.feed.handler.ItemHandler
import com.munzenberger.feed.source.XMLFeedSource
import java.net.URI
import java.util.function.Consumer

class FeedProcessorFactory(
    private val registryFactory: ItemRegistryFactory,
    private val itemFilterFactory: ItemProcessorFactory<ItemFilter> = DefaultItemProcessorFactory(),
    private val itemHandlerFactory: ItemProcessorFactory<ItemHandler> = DefaultItemProcessorFactory(),
    private val eventConsumer: Consumer<FeedEvent>,
) {
    fun getInstance(feedConfig: FeedConfig): FeedProcessor {
        val url = URI(feedConfig.url).toURL()

        val source =
            XMLFeedSource(
                source = url,
                userAgent = feedConfig.userAgent,
            )

        val itemRegistry = registryFactory.getInstance(feedConfig)

        val itemFilter = CompositeItemFilter(feedConfig.filters.map(itemFilterFactory::getInstance))

        val itemHandler = CompositeItemHandler(feedConfig.handlers.map(itemHandlerFactory::getInstance))

        return FeedProcessor(
            source,
            itemRegistry,
            itemFilter,
            itemHandler,
            eventConsumer,
        )
    }
}
