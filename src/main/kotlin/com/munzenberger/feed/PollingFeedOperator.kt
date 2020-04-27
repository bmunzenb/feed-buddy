package com.munzenberger.feed

import com.munzenberger.feed.config.AppConfigProvider
import com.munzenberger.feed.config.FeedProcessorFactory
import com.munzenberger.feed.engine.pluralize
import java.util.Timer
import java.util.TimerTask

class PollingFeedOperator(
        private val configProvider: AppConfigProvider,
        private val processorFactory: FeedProcessorFactory
) : FeedOperator {

    private var timer: Timer? = null

    override fun start() {

        val config = configProvider.config

        with(config.feeds.size) {
            println("Scheduling $this ${"feed".pluralize(this)} from ${configProvider.name}.")
        }

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
