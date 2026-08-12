package com.munzenberger.feed

import com.munzenberger.feed.config.ConfigProvider
import com.munzenberger.feed.config.OperatorConfig
import com.munzenberger.feed.engine.FeedProcessorFactory
import com.munzenberger.feed.engine.ItemProcessorFactory
import com.munzenberger.feed.engine.ItemRegistryFactory
import com.munzenberger.feed.filter.ItemFilter
import com.munzenberger.feed.handler.ItemHandler
import java.util.Timer
import java.util.TimerTask
import java.util.function.Consumer
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

class PollingFeedOperator(
    registryFactory: ItemRegistryFactory,
    private val configProvider: ConfigProvider,
    filterFactory: ItemProcessorFactory<ItemFilter>,
    handlerFactory: ItemProcessorFactory<ItemHandler>,
    private val eventConsumer: Consumer<FeedEvent>,
) : BaseFeedOperator(registryFactory, configProvider, filterFactory, handlerFactory, eventConsumer) {
    private var timer: Timer? = null

    override fun start(
        config: OperatorConfig,
        processorFactory: FeedProcessorFactory,
    ) {
        val tasks: List<Triple<TimerTask, Long, Long>> =
            config.feeds.map {
                val processor = processorFactory.getInstance(it)

                val task =
                    object : TimerTask() {
                        override fun run() {
                            processor.run()
                        }
                    }

                val delay = (it.delay ?: config.delay).minutes
                val period = (it.period ?: config.period).minutes

                Triple(task, delay.inWholeMilliseconds, period.inWholeMilliseconds)
            }

        val configurationChangeTask =
            object : TimerTask() {
                private val timestamp = configProvider.timestamp

                override fun run() {
                    if (configProvider.timestamp != timestamp) {
                        eventConsumer.accept(SystemEvent.OperatorConfigurationChange)
                        this@PollingFeedOperator.run {
                            cancel()
                            start()
                        }
                    }
                }
            }

        timer =
            Timer().apply {
                tasks.forEach {
                    schedule(it.first, it.second, it.third)
                }

                // check for configuration changes every 5 seconds
                val configCheckIntervalMs = 5.seconds.inWholeMilliseconds
                schedule(configurationChangeTask, configCheckIntervalMs, configCheckIntervalMs)
            }
    }

    override fun cancel() {
        timer?.cancel()
    }
}
