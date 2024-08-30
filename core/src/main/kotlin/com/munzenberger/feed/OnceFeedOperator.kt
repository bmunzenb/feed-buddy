package com.munzenberger.feed

import com.munzenberger.feed.config.OperatorConfig
import com.munzenberger.feed.config.ConfigProvider
import com.munzenberger.feed.engine.FeedProcessorFactory
import com.munzenberger.feed.engine.ItemProcessorFactory
import com.munzenberger.feed.engine.ItemRegistryFactory
import com.munzenberger.feed.filter.ItemFilter
import com.munzenberger.feed.handler.ItemHandler
import com.munzenberger.feed.status.FeedStatus
import java.util.function.Consumer

class OnceFeedOperator(
    registryFactory: ItemRegistryFactory,
    configProvider: ConfigProvider,
    filterFactory: ItemProcessorFactory<ItemFilter>,
    handlerFactory: ItemProcessorFactory<ItemHandler>,
    statusConsumer: Consumer<FeedStatus>
) : BaseFeedOperator(registryFactory, configProvider, filterFactory, handlerFactory, statusConsumer) {

    override fun start(config: OperatorConfig, processorFactory: FeedProcessorFactory) {
        config.feeds.map(processorFactory::getInstance).forEach { it.run() }
    }
}
