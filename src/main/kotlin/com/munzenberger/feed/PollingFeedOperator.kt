package com.munzenberger.feed

import com.munzenberger.feed.config.AppConfig
import com.munzenberger.feed.config.AppConfigProvider
import com.munzenberger.feed.config.FeedProcessorFactory
import com.munzenberger.feed.config.ItemProcessorFactory
import com.munzenberger.feed.filter.ItemFilter
import com.munzenberger.feed.handler.ItemHandler
import java.nio.file.Path
import java.util.Timer
import java.util.TimerTask

class PollingFeedOperator(
        registryDirectory: Path,
        private val configProvider: AppConfigProvider,
        filterFactory: ItemProcessorFactory<ItemFilter>,
        handlerFactory: ItemProcessorFactory<ItemHandler>
) : BaseFeedOperator(registryDirectory, configProvider, filterFactory, handlerFactory) {

    private var timer: Timer? = null

    override fun start(config: AppConfig, processorFactory: FeedProcessorFactory) {

        val tasks: List<Pair<TimerTask, Long>> = config.feeds.map {

            val feed = processorFactory.getInstance(it)

            val task = object : TimerTask() {
                override fun run() {
                    feed.execute()
                }
            }

            val period = (it.period ?: config.period).toLong() * 60 * 1000 // convert from minutes to millis

            task to period
        }

        val configurationChangeTask = object : TimerTask() {
            private val timestamp = configProvider.timestamp
            override fun run() {
                if (configProvider.timestamp != timestamp) {
                    println("Detected configuration change.")
                    cancel()
                    start()
                }
            }
        }

        timer = Timer().apply {

            tasks.forEach {
                schedule(it.first, 0, it.second)
            }

            // check for configuration changes every 5 seconds
            schedule(configurationChangeTask, 5000, 5000)
        }
    }

    override fun cancel() {
        timer?.cancel()
    }
}
