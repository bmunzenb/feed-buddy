package com.munzenberger.feed

import com.munzenberger.feed.config.AppConfig
import com.munzenberger.feed.config.AppConfigProvider
import com.munzenberger.feed.config.FeedProcessorFactory
import com.munzenberger.feed.config.ItemProcessorFactory
import com.munzenberger.feed.filter.ItemFilter
import com.munzenberger.feed.handler.ItemHandler

class OnceFeedOperator(
        configProvider: AppConfigProvider,
        filterFactory: ItemProcessorFactory<ItemFilter>,
        handlerFactory: ItemProcessorFactory<ItemHandler>
) : BaseFeedOperator(configProvider, filterFactory, handlerFactory) {

    override fun start(config: AppConfig, processorFactory: FeedProcessorFactory) {
        config.feeds.map(processorFactory::getInstance).forEach { it.execute() }
    }
}
