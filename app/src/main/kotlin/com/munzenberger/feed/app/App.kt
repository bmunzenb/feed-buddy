package com.munzenberger.feed.app

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.main
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.enum
import com.github.ajalt.clikt.parameters.types.int
import com.github.ajalt.clikt.parameters.types.path
import com.munzenberger.feed.FeedOperator
import com.munzenberger.feed.OnceFeedOperator
import com.munzenberger.feed.PollingFeedOperator
import com.munzenberger.feed.client.URLClientDefaults
import com.munzenberger.feed.config.FileConfigProvider
import com.munzenberger.feed.config.ItemProcessorConfig
import com.munzenberger.feed.engine.DefaultItemProcessorFactory
import com.munzenberger.feed.engine.FileItemRegistryFactory
import com.munzenberger.feed.engine.ItemProcessorFactory
import com.munzenberger.feed.filter.ItemFilter
import com.munzenberger.feed.handler.ItemHandler
import java.nio.file.Path
import java.nio.file.Paths
import java.util.Properties
import kotlin.time.Duration.Companion.seconds

fun main(args: Array<String>) {
    val versionProperties =
        Properties().apply {
            val inStream = ClassLoader.getSystemResourceAsStream("version.properties")
            load(inStream)
        }

    versionProperties["version"].let {
        println("Feed Buddy version $it (https://github.com/bmunzenb/feed-buddy)")
        URLClientDefaults.userAgent = "Feed-Buddy/$it (+https://github.com/bmunzenb/feed-buddy)"
    }

    System.getProperties().let {
        val runtimeName = it["java.runtime.name"]
        val runtimeVersion = it["java.runtime.version"]
        println("$runtimeName $runtimeVersion")
    }

    println()

    App().main(args)
}

enum class OperatingMode {
    POLL,
    ONCE,
    NOOP,
}

class App : CliktCommand(name = "feed-buddy") {
    private val feeds: Path by argument(help = "Path to feeds configuration file")
        .path(mustBeReadable = true, mustExist = true, canBeDir = false)

    private val registry: Path by option("-r", "--registry", help = "Path to processed items registry")
        .path(mustBeWritable = true, mustExist = true, canBeFile = false)
        .default(value = Paths.get("."), defaultForHelp = "Current working directory")

    private val mode: OperatingMode by option("-m", "--mode", help = "Sets the operating mode")
        .enum<OperatingMode>()
        .default(OperatingMode.POLL, defaultForHelp = "POLL")

    private val timeout: Int by option("-t", "--timeout", help = "Sets the read timeout in seconds")
        .int()
        .default(value = 30_000, defaultForHelp = "30")

    private val output: Path? by option("-o", "--output", help = "Path to output file")
        .path(canBeDir = false)

    override fun run() {
        URLClientDefaults.timeout = timeout.seconds

        val logger =
            CompositeLogger().apply {
                add(ConsoleLogger)
                output?.toFile()?.let {
                    it.createNewFile()
                    add(FileLogger(it))
                }
            }

        val registryFactory = FileItemRegistryFactory(registry)

        val configProvider = FileConfigProvider(feeds.toFile())

        val filterFactory = DefaultItemProcessorFactory<ItemFilter>()

        val handlerFactory: ItemProcessorFactory<ItemHandler> =
            when (mode) {
                OperatingMode.NOOP -> {
                    logger.println("Executing in NOOP mode: items will be marked as processed but no handlers will execute.")
                    object : ItemProcessorFactory<ItemHandler> {
                        override fun getInstance(config: ItemProcessorConfig) = ItemHandler { _, _, _ -> }
                    }
                }

                else ->
                    DefaultItemProcessorFactory()
            }

        val eventConsumer = LoggingEventConsumer(logger)

        val feedOperator: FeedOperator =
            when (mode) {
                OperatingMode.POLL ->
                    PollingFeedOperator(registryFactory, configProvider, filterFactory, handlerFactory, eventConsumer)

                OperatingMode.ONCE, OperatingMode.NOOP ->
                    OnceFeedOperator(registryFactory, configProvider, filterFactory, handlerFactory, eventConsumer)
            }

        Runtime.getRuntime().addShutdownHook(
            object : Thread() {
                override fun run() {
                    logger.print("Shutting down... ")
                    feedOperator.cancel()
                    logger.println("done.")
                }
            },
        )

        feedOperator.start()
    }
}
