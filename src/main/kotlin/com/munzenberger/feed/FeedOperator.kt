package com.munzenberger.feed

import com.munzenberger.feed.config.AppConfig
import com.munzenberger.feed.config.AppConfigProvider
import com.munzenberger.feed.config.FeedConfig
import com.munzenberger.feed.config.FeedProcessorFactory
import com.munzenberger.feed.config.ItemProcessorFactory
import com.munzenberger.feed.engine.pluralize
import com.munzenberger.feed.filter.ItemFilter
import com.munzenberger.feed.handler.ItemHandler
import java.nio.file.Path

interface FeedOperator {
    fun start()
    fun cancel() {}
}

abstract class BaseFeedOperator(
        private val registryDirectory: Path,
        private val configProvider: AppConfigProvider,
        private val filterFactory: ItemProcessorFactory<ItemFilter>,
        private val handlerFactory: ItemProcessorFactory<ItemHandler>
) : FeedOperator {

    override fun start() {

        val config = configProvider.config

        filterFactory.reset()
        handlerFactory.reset()

        // global filters & handlers
        config.filters.map(filterFactory::getInstance)
        config.handlers.map(handlerFactory::getInstance)

        with(config.feeds.size) {
            println("Scheduling $this ${"feed".pluralize(this)} from ${configProvider.name}.")
        }

        val processorFactory = FeedProcessorFactory(registryDirectory, filterFactory, handlerFactory)

        start(config, processorFactory)
    }

    abstract fun start(config: AppConfig, processorFactory: FeedProcessorFactory)
}
