package com.munzenberger.feed

import com.munzenberger.feed.config.JsonAppConfigAdapter
import com.munzenberger.feed.config.FeedProcessorFactory
import com.munzenberger.feed.config.ItemProcessorFactory
import com.munzenberger.feed.handler.ItemHandler
import java.io.File
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

    if (args.isNotEmpty()) {
        // assume the first argument is a config file

        val file = File(args[0])

        val config = JsonAppConfigAdapter.read(file)

        val itemHandlerFactory = ItemProcessorFactory<ItemHandler>()
        val processorFactory = FeedProcessorFactory(itemHandlerFactory)

        config.feeds.forEach { feedConfig ->

            val processor = processorFactory.getInstance(feedConfig)
            processor.execute()
        }
    }
}
