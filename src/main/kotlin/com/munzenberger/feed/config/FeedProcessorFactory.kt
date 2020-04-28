package com.munzenberger.feed.config

import com.munzenberger.feed.FeedContext
import com.munzenberger.feed.Item
import com.munzenberger.feed.engine.FeedProcessor
import com.munzenberger.feed.engine.FileItemRegistry
import com.munzenberger.feed.filter.ItemFilter
import com.munzenberger.feed.handler.ItemHandler
import com.munzenberger.feed.source.XMLFeedSource
import java.net.URL
import java.nio.file.Path

class FeedProcessorFactory(
        private val registryDirectory: Path,
        private val itemFilterFactory: ItemProcessorFactory<ItemFilter> = DefaultItemProcessorFactory(),
        private val itemHandlerFactory: ItemProcessorFactory<ItemHandler> = DefaultItemProcessorFactory()
) {

    fun getInstance(feedConfig: FeedConfig): FeedProcessor {

        val url = URL(feedConfig.url)

        val source = XMLFeedSource(
                source = url,
                userAgent = feedConfig.userAgent)

        val itemRegistry = FileItemRegistry(registryDirectory.resolve(url.registryFilename))

        val itemFilter = object : ItemFilter {
            private val filters = feedConfig.filters.map(itemFilterFactory::getInstance)
            override fun evaluate(context: FeedContext, item: Item): Boolean {
                return filters.all { it.evaluate(context, item) }
            }
        }

        val itemHandler = object : ItemHandler {
            private val handlers = feedConfig.handlers.map(itemHandlerFactory::getInstance)
            override fun execute(context: FeedContext, item: Item) {
                handlers.forEach { it.execute(context, item) }
            }
        }

        return FeedProcessor(source, itemRegistry, itemFilter, itemHandler)
    }
}

private fun String.filteredForPath(): String {
    val invalidChars = "<>:\"/\\|?*="
    val sb = StringBuilder(this)
    for (i in sb.indices) {
        if (sb[i] in invalidChars) {
            sb[i] = '-'
        }
    }
    return sb.toString()
}

private val URL.registryFilename: String
    get() = host.filteredForPath() + file.filteredForPath() + ".processed"
