package com.munzenberger.feed

import com.munzenberger.feed.config.AppConfig
import com.munzenberger.feed.config.AppConfigProvider
import com.munzenberger.feed.config.FeedProcessorFactory
import com.munzenberger.feed.config.ItemProcessorFactory
import com.munzenberger.feed.engine.ItemRegistryFactory
import com.munzenberger.feed.filter.ItemFilter
import com.munzenberger.feed.handler.ItemHandler
import com.munzenberger.feed.status.FeedStatus
import java.nio.file.Path
import java.util.function.Consumer

class OnceFeedOperator(
        registryFactory: ItemRegistryFactory,
        configProvider: AppConfigProvider,
        filterFactory: ItemProcessorFactory<ItemFilter>,
        handlerFactory: ItemProcessorFactory<ItemHandler>,
        statusConsumer: Consumer<FeedStatus>
) : BaseFeedOperator(registryFactory, configProvider, filterFactory, handlerFactory, statusConsumer) {

    override fun start(config: AppConfig, processorFactory: FeedProcessorFactory) {
        config.feeds.map(processorFactory::getInstance).forEach { it.run() }
    }
}
