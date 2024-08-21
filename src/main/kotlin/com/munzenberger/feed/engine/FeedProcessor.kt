package com.munzenberger.feed.engine

import com.munzenberger.feed.FeedContext
import com.munzenberger.feed.filter.ItemFilter
import com.munzenberger.feed.handler.ItemHandler
import com.munzenberger.feed.source.FeedSource
import com.munzenberger.feed.status.FeedStatus
import com.munzenberger.feed.Logger
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

            val consumerLogger = ConsumerLogger(statusConsumer)

            val items = feed.items
                    .filterNot(itemRegistry::contains)
                    .filter { itemFilter.evaluate(context, it, consumerLogger) }

            items.forEachIndexed { index, item ->

                statusConsumer.accept(FeedStatus.ProcessorItemStart(index, items.size, item.title, item.guid))

                try {
                    itemHandler.execute(context, item, consumerLogger)
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

private class ConsumerLogger(
    private val consumer: Consumer<FeedStatus>
) : Logger {

    override fun print(obj: Any) {
        consumer.accept(FeedStatus.HandlerMessage(obj, true))
    }

    override fun println(obj: Any) {
        consumer.accept(FeedStatus.HandlerMessage(obj))
    }

    override fun printStackTrace(t: Throwable) {
        consumer.accept(FeedStatus.HandlerError(t))
    }
}
