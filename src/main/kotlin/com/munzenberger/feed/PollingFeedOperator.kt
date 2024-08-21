package com.munzenberger.feed

import com.munzenberger.feed.config.AppConfig
import com.munzenberger.feed.config.AppConfigProvider
import com.munzenberger.feed.engine.FeedProcessorFactory
import com.munzenberger.feed.engine.ItemProcessorFactory
import com.munzenberger.feed.engine.ItemRegistryFactory
import com.munzenberger.feed.filter.ItemFilter
import com.munzenberger.feed.handler.ItemHandler
import com.munzenberger.feed.status.FeedStatus
import java.util.Timer
import java.util.TimerTask
import java.util.function.Consumer
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

class PollingFeedOperator(
    registryFactory: ItemRegistryFactory,
    private val configProvider: AppConfigProvider,
    filterFactory: ItemProcessorFactory<ItemFilter>,
    handlerFactory: ItemProcessorFactory<ItemHandler>,
    private val statusConsumer: Consumer<FeedStatus>
) : BaseFeedOperator(registryFactory, configProvider, filterFactory, handlerFactory, statusConsumer) {

    private var timer: Timer? = null

    override fun start(config: AppConfig, processorFactory: FeedProcessorFactory) {

        val tasks: List<Pair<TimerTask, Long>> = config.feeds.map {

            val processor = processorFactory.getInstance(it)

            val task = object : TimerTask() {
                override fun run() {
                    processor.run()
                }
            }

            val period = (it.period ?: config.period).minutes

            task to period.inWholeMilliseconds
        }

        val configurationChangeTask = object : TimerTask() {
            private val timestamp = configProvider.timestamp
            override fun run() {
                if (configProvider.timestamp != timestamp) {
                    statusConsumer.accept(FeedStatus.OperatorConfigurationChange)
                    this@PollingFeedOperator.run {
                        cancel()
                        start()
                    }
                }
            }
        }

        timer = Timer().apply {

            tasks.forEach {
                schedule(it.first, 0, it.second)
            }

            // check for configuration changes every 5 seconds
            val delayAndPeriod = 5.seconds.inWholeMilliseconds
            schedule(configurationChangeTask, delayAndPeriod, delayAndPeriod)
        }
    }

    override fun cancel() {
        timer?.cancel()
    }
}
