package com.munzenberger.feed.engine

import com.munzenberger.feed.FeedContext
import com.munzenberger.feed.filter.ItemFilter
import com.munzenberger.feed.handler.ItemHandler
import com.munzenberger.feed.source.FeedSource
import com.munzenberger.feed.status.FeedStatus
import java.util.function.Consumer

class FeedProcessor(
        private val source: FeedSource,
        private val itemRegistry: ItemRegistry,
        private val itemFilter: ItemFilter,
        private val itemHandler: ItemHandler,
        private val statusConsumer: Consumer<FeedStatus>
) : Runnable {

    override fun run() {
        try {
            statusConsumer.accept(FeedStatus.ProcessorFeedStart(source.name))

            val startTime = System.currentTimeMillis()
            val feed = source.read()

            statusConsumer.accept(FeedStatus.ProcessorFeedRead(feed.title, feed.items.size))

            val context = FeedContext(source.name, feed.title)

            var processed = 0
            var errors = 0

            val items = feed.items
                    .filterNot(itemRegistry::contains)
                    .filter { itemFilter.evaluate(context, it, statusConsumer) }

            items.forEachIndexed { index, item ->

                statusConsumer.accept(FeedStatus.ProcessorItemStart(index, items.size, item.title, item.guid))

                try {
                    itemHandler.execute(context, item, statusConsumer)
                    itemRegistry.add(item)
                    processed++
                } catch (e: Throwable) {
                    statusConsumer.accept(FeedStatus.ProcessorItemError(e))
                    errors++
                }
            }

            val elapsed = System.currentTimeMillis() - startTime
            statusConsumer.accept(FeedStatus.ProcessorFeedComplete(items.size, processed, errors, elapsed))

        } catch (e: Throwable) {
            statusConsumer.accept(FeedStatus.ProcessorFeedError(e))
        }
    }
}
