package com.munzenberger.feed

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.path
import com.munzenberger.feed.config.AppConfigProvider
import com.munzenberger.feed.config.FeedProcessorFactory
import com.munzenberger.feed.config.FileAppConfigProvider
import com.munzenberger.feed.engine.pluralize
import java.io.File
import java.util.Properties
import java.util.Timer
import java.util.TimerTask

fun main(args: Array<String>) {

    val versionProperties = Properties().apply {
        val inStream = ClassLoader.getSystemResourceAsStream("version.properties")
        load(inStream)
    }

    val version = versionProperties["version"]

    println("Feed Buddy version $version (https://github.com/bmunzenb/feed-buddy)")

    System.setProperty("http.agent", "Feed-Buddy/$version (+https://github.com/bmunzenb/feed-buddy)")
    System.setProperty("sun.net.client.defaultConnectTimeout", "30000")
    System.setProperty("sun.net.client.defaultReadTimeout", "30000")

    App().main(args)
}

class App : CliktCommand() {

    private val feeds by option(help = "Path to feeds configuration file")
            .path(mustBeReadable = true, mustExist = true, canBeDir = false)

    private lateinit var file: File
    private lateinit var configProvider: AppConfigProvider
    private lateinit var processorFactory: FeedProcessorFactory

    private var timer: Timer? = null

    override fun run() {

        file = feeds?.toFile() ?: File("feeds.xml")

        configProvider = FileAppConfigProvider(file)

        processorFactory = FeedProcessorFactory()

        Runtime.getRuntime().addShutdownHook(object : Thread() {
            override fun run() {
                timer?.cancel()
            }
        })

        startPolling()
    }

    private fun startPolling() {

        timer?.cancel()

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

        val configurationChangeTask = ConfigurationChangeTask(file) {
            println("Detected configuration change.")
            startPolling()
        }

        timer = Timer().apply {

            tasks.forEach { schedule(it.first, 0, it.second) }

            // check for configuration changes every 5 seconds
            schedule(configurationChangeTask, 5000, 5000)
        }
    }
}
