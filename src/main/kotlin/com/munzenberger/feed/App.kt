package com.munzenberger.feed

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.enum
import com.github.ajalt.clikt.parameters.types.int
import com.github.ajalt.clikt.parameters.types.path
import com.munzenberger.feed.config.DefaultItemProcessorFactory
import com.munzenberger.feed.config.FeedProcessorFactory
import com.munzenberger.feed.config.FileAppConfigProvider
import com.munzenberger.feed.config.ItemProcessorConfig
import com.munzenberger.feed.config.ItemProcessorFactory
import com.munzenberger.feed.handler.ItemHandler
import java.nio.file.Path
import java.util.Properties
import kotlin.system.exitProcess

fun main(args: Array<String>) {

    val versionProperties = Properties().apply {
        val inStream = ClassLoader.getSystemResourceAsStream("version.properties")
        load(inStream)
    }

    val version = versionProperties["version"]

    println("Feed Buddy version $version (https://github.com/bmunzenb/feed-buddy)")

    System.setProperty("http.agent", "Feed-Buddy/$version (+https://github.com/bmunzenb/feed-buddy)")

    App().main(args)
}

enum class OperatingMode {
    POLL, ONCE, NOOP
}

class App : CliktCommand() {

    private val feeds: Path by argument(help = "Path to feeds configuration file")
            .path(mustBeReadable = true, mustExist = true, canBeDir = false)

    private val mode: OperatingMode by option("-m", "--mode", help = "Sets the operating mode")
            .enum<OperatingMode>()
            .default(OperatingMode.POLL)

    private val timeout: Int by option("-t", "--timeout", help = "Sets the timeout in seconds")
            .int()
            .default(value = 30000, defaultForHelp = "30")

    override fun run() {

        System.setProperty("sun.net.client.defaultConnectTimeout", (timeout * 1000).toString())
        System.setProperty("sun.net.client.defaultReadTimeout", (timeout * 1000).toString())

        val configFile = feeds.toFile()

        when {
            !configFile.exists() -> {
                println("Configuration file not found: $configFile")
                exitProcess(1)
            }
            !configFile.canRead() -> {
                println("Configuration file not readable: $configFile")
                exitProcess(1)
            }
        }

        val configProvider = FileAppConfigProvider(configFile)

        val handlerFactory: ItemProcessorFactory<ItemHandler> = when (mode) {
            OperatingMode.NOOP -> {
                println("Executing in NOOP mode: items will be marked as processed but no handlers will execute.")
                object : ItemProcessorFactory<ItemHandler> {
                    override fun getInstance(config: ItemProcessorConfig): ItemHandler {
                        return object : ItemHandler {
                            override fun execute(item: Item) {}
                        }
                    }
                }
            }
            else -> DefaultItemProcessorFactory()
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
