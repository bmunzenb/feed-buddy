package com.munzenberger.feed

import com.munzenberger.feed.config.AppConfigProvider
import com.munzenberger.feed.config.FeedProcessorFactory
import com.munzenberger.feed.engine.pluralize

class OnceFeedOperator(
        private val configProvider: AppConfigProvider,
        private val processorFactory: FeedProcessorFactory
) : FeedOperator {

    override fun start() {

        val config = configProvider.config

        with(config.feeds.size) {
            println("Processing $this ${"feed".pluralize(this)} from ${configProvider.name}.")
        }

        config.feeds.map(processorFactory::getInstance).forEach { it.execute() }
    }
}
