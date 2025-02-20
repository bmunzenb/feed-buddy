package com.munzenberger.feed

import com.munzenberger.feed.config.ConfigProvider
import com.munzenberger.feed.config.OperatorConfig
import com.munzenberger.feed.engine.FeedProcessorFactory
import com.munzenberger.feed.engine.ItemProcessorFactory
import com.munzenberger.feed.engine.ItemRegistryFactory
import com.munzenberger.feed.filter.ItemFilter
import com.munzenberger.feed.handler.ItemHandler
import java.util.function.Consumer

interface FeedOperator {
    fun start()

    fun cancel() {}
}

abstract class BaseFeedOperator(
    private val registryFactory: ItemRegistryFactory,
    private val configProvider: ConfigProvider,
    private val filterFactory: ItemProcessorFactory<ItemFilter>,
    private val handlerFactory: ItemProcessorFactory<ItemHandler>,
    private val eventConsumer: Consumer<FeedEvent>,
) : FeedOperator {
    override fun start() {
        val config = configProvider.config

        filterFactory.reset()
        handlerFactory.reset()

        // global filters & handlers
        config.filters.map(filterFactory::getInstance)
        config.handlers.map(handlerFactory::getInstance)

        val processorFactory =
            FeedProcessorFactory(
                registryFactory,
                filterFactory,
                handlerFactory,
                eventConsumer,
            )

        eventConsumer.accept(FeedEvent.OperatorStart(config.feeds.size, configProvider.name))

        start(config, processorFactory)
    }

    abstract fun start(
        config: OperatorConfig,
        processorFactory: FeedProcessorFactory,
    )
}
