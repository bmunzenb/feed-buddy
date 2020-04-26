package com.munzenberger.feed.config

import com.munzenberger.feed.Item
import com.munzenberger.feed.engine.FeedProcessor
import com.munzenberger.feed.engine.FileItemRegistry
import com.munzenberger.feed.handler.ItemHandler
import com.munzenberger.feed.source.XMLFeedSource
import java.net.URL
import java.nio.file.Path
import java.nio.file.Paths
import javax.xml.parsers.DocumentBuilderFactory

class FeedProcessorFactory(private val itemHandlerFactory: ItemHandlerFactory) {

    private val documentBuilderFactory = DocumentBuilderFactory.newInstance()

    fun getInstance(feedConfig: FeedConfig): FeedProcessor {

        val url = URL(feedConfig.url)

        val source = XMLFeedSource(
                source = url,
                userAgent = feedConfig.userAgent,
                documentBuilderFactory = documentBuilderFactory)

        val itemRegistry = FileItemRegistry(url.registryFilePath)

        val itemHandler = object : ItemHandler {
            private val handlers = feedConfig.handlers.map(itemHandlerFactory::getInstance)
            override fun execute(item: Item) {
                handlers.forEach { it.execute(item) }
            }
        }

        return FeedProcessor(source, itemRegistry, itemHandler)
    }
}

private fun String.filteredForPath(): String {
    val invalidChars = "<>:\"/\\|?*"
    val sb = StringBuilder(this)
    for (i in sb.indices) {
        if (sb[i] in invalidChars) {
            sb[i] = '-'
        }
    }
    return sb.toString()
}

private val URL.registryFilePath: Path
    get() {
        val filename = host.filteredForPath() + file.filteredForPath() + ".processed"
        return Paths.get(".", filename)
    }
