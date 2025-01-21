package com.munzenberger.feed.engine

import com.munzenberger.feed.FeedContext
import com.munzenberger.feed.Logger
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
    private val statusConsumer: Consumer<FeedStatus>,
) : Runnable {
    override fun run() {
        try {
            statusConsumer.accept(FeedStatus.ProcessorFeedStart(source.name))

            val feed = source.read()

            statusConsumer.accept(FeedStatus.ProcessorFeedRead(feed.title, feed.items.size))

            val context = FeedContext(source.name, feed.title)
            val consumerLogger = ConsumerLogger(statusConsumer)

            val items =
                feed.items
                    .filterNot(itemRegistry::contains)
                    .filter { itemFilter.evaluate(context, it, consumerLogger) }

            statusConsumer.accept(FeedStatus.ProcessorFeedFilter(items.size))

            items.forEach { item ->
                try {
                    statusConsumer.accept(FeedStatus.ProcessorItemStart(item.title, item.guid))
                    itemHandler.execute(context, item, consumerLogger)
                    itemRegistry.add(item)
                } catch (e: Throwable) {
                    statusConsumer.accept(FeedStatus.ProcessorItemError(e))
                } finally {
                    statusConsumer.accept(FeedStatus.ProcessorItemComplete)
                }
            }
        } catch (e: Throwable) {
            statusConsumer.accept(FeedStatus.ProcessorFeedError(e))
        } finally {
            statusConsumer.accept(FeedStatus.ProcessorFeedComplete)
        }
    }
}

private class ConsumerLogger(
    private val consumer: Consumer<FeedStatus>,
) : Logger {
    override fun print(obj: Any) {
        consumer.accept(FeedStatus.ItemProcessorMessage(obj, true))
    }

    override fun println(obj: Any) {
        consumer.accept(FeedStatus.ItemProcessorMessage(obj))
    }

    override fun printStackTrace(t: Throwable) {
        consumer.accept(FeedStatus.ItemProcessorError(t))
    }
}
