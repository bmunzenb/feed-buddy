package com.munzenberger.feed

import com.munzenberger.feed.config.AppConfig
import com.munzenberger.feed.config.AppConfigProvider
import com.munzenberger.feed.engine.FeedProcessorFactory
import com.munzenberger.feed.engine.ItemProcessorFactory
import com.munzenberger.feed.engine.ItemRegistryFactory
import com.munzenberger.feed.filter.ItemFilter
import com.munzenberger.feed.handler.ItemHandler
import com.munzenberger.feed.status.FeedStatus
import java.util.function.Consumer

interface FeedOperator {
    fun start()
    fun cancel() {}
}

abstract class BaseFeedOperator(
    private val registryFactory: ItemRegistryFactory,
    private val configProvider: AppConfigProvider,
    private val filterFactory: ItemProcessorFactory<ItemFilter>,
    private val handlerFactory: ItemProcessorFactory<ItemHandler>,
    private val statusConsumer: Consumer<FeedStatus>
) : FeedOperator {

    override fun start() {

        val config = configProvider.config

        filterFactory.reset()
        handlerFactory.reset()

        // global filters & handlers
        config.filters.map(filterFactory::getInstance)
        config.handlers.map(handlerFactory::getInstance)

        statusConsumer.accept(FeedStatus.OperatorStart(config.feeds.size, configProvider.name))

        val processorFactory = FeedProcessorFactory(
            registryFactory,
            filterFactory,
            handlerFactory,
            statusConsumer
        )

        start(config, processorFactory)
    }

    abstract fun start(config: AppConfig, processorFactory: FeedProcessorFactory)
}
