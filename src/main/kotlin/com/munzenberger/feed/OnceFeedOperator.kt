package com.munzenberger.feed

import com.munzenberger.feed.config.AppConfig
import com.munzenberger.feed.config.AppConfigProvider
import com.munzenberger.feed.config.FeedProcessorFactory
import com.munzenberger.feed.config.ItemProcessorFactory
import com.munzenberger.feed.filter.ItemFilter
import com.munzenberger.feed.handler.ItemHandler
import java.nio.file.Path

class OnceFeedOperator(
        registryDirectory: Path,
        configProvider: AppConfigProvider,
        filterFactory: ItemProcessorFactory<ItemFilter>,
        handlerFactory: ItemProcessorFactory<ItemHandler>,
        logger: Logger
) : BaseFeedOperator(registryDirectory, configProvider, filterFactory, handlerFactory, logger) {

    override fun start(config: AppConfig, processorFactory: FeedProcessorFactory) {
        config.feeds.map(processorFactory::getInstance).forEach { it.run() }
    }
}
