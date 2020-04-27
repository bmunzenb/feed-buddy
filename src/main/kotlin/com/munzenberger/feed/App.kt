package com.munzenberger.feed

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.enum
import com.github.ajalt.clikt.parameters.types.path
import com.munzenberger.feed.config.BaseItemProcessorFactory
import com.munzenberger.feed.config.FeedProcessorFactory
import com.munzenberger.feed.config.FileAppConfigProvider
import com.munzenberger.feed.config.ItemProcessorConfig
import com.munzenberger.feed.config.ItemProcessorFactory
import com.munzenberger.feed.handler.ItemHandler
import java.nio.file.Path
import java.nio.file.Paths
import java.util.Properties

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

enum class OperatingMode {
    POLL, ONCE, NOOP
}

class App : CliktCommand() {

    private val feeds: Path by option(help = "Path to feeds configuration file")
            .path(mustBeReadable = true, mustExist = true, canBeDir = false)
            .default(Paths.get("feeds.xml"))

    private val mode: OperatingMode by option(help = "Sets the operating mode")
            .enum<OperatingMode>()
            .default(OperatingMode.POLL)

    override fun run() {

        val configFile = feeds.toFile()

        val configProvider = FileAppConfigProvider(configFile)

        if (mode == OperatingMode.NOOP) {
            println("Executing in NOOP mode: items will be marked as processed but no handlers will execute.")
        }

        val handlerFactory: ItemProcessorFactory<ItemHandler> = when (mode) {
            OperatingMode.NOOP -> object : ItemProcessorFactory<ItemHandler> {
                override fun getInstance(config: ItemProcessorConfig): ItemHandler {
                    return object : ItemHandler {
                        override fun execute(item: Item) {}
                    }
                }
            }
            else -> BaseItemProcessorFactory()
        }

        val processorFactory = FeedProcessorFactory(itemHandlerFactory = handlerFactory)

        val feedOperator: FeedOperator = when (mode) {
            OperatingMode.POLL -> PollingFeedOperator(configProvider, processorFactory)
            OperatingMode.ONCE, OperatingMode.NOOP -> OnceFeedOperator(configProvider, processorFactory)
        }

        Runtime.getRuntime().addShutdownHook(object : Thread() {
            override fun run() {
                feedOperator.cancel()
            }
        })

        feedOperator.start()
    }
}
