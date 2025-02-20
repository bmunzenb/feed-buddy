package com.munzenberger.feed.engine

import com.munzenberger.feed.FeedContext
import com.munzenberger.feed.FeedEvent
import com.munzenberger.feed.Logger
import com.munzenberger.feed.filter.ItemFilter
import com.munzenberger.feed.handler.ItemHandler
import com.munzenberger.feed.source.FeedSource
import java.util.function.Consumer

class FeedProcessor(
    private val source: FeedSource,
    private val itemRegistry: ItemRegistry,
    private val itemFilter: ItemFilter,
    private val itemHandler: ItemHandler,
    private val statusConsumer: Consumer<FeedEvent>,
) : Runnable {
    override fun run() {
        try {
            statusConsumer.accept(FeedEvent.ProcessorFeedStart(source.name))

            val feed = source.read()

            statusConsumer.accept(FeedEvent.ProcessorFeedRead(feed.title, feed.items.size))

            val context = FeedContext(source.name, feed.title)
            val consumerLogger = ConsumerLogger(statusConsumer)

            val items =
                feed.items
                    .filterNot(itemRegistry::contains)
                    .filter { itemFilter.evaluate(context, it, consumerLogger) }

            statusConsumer.accept(FeedEvent.ProcessorFeedFilter(items.size))

            items.forEach { item ->
                try {
                    statusConsumer.accept(FeedEvent.ProcessorItemStart(item.title, item.guid))
                    itemHandler.execute(context, item, consumerLogger)
                    itemRegistry.add(item)
                } catch (e: Throwable) {
                    statusConsumer.accept(FeedEvent.ProcessorItemError(e))
                } finally {
                    statusConsumer.accept(FeedEvent.ProcessorItemComplete)
                }
            }
        } catch (e: Throwable) {
            statusConsumer.accept(FeedEvent.ProcessorFeedError(e))
        } finally {
            statusConsumer.accept(FeedEvent.ProcessorFeedComplete)
        }
    }
}

private class ConsumerLogger(
    private val consumer: Consumer<FeedEvent>,
) : Logger {
    override fun print(obj: Any) {
        consumer.accept(FeedEvent.ItemProcessorMessage(obj, true))
    }

    override fun println(obj: Any) {
        consumer.accept(FeedEvent.ItemProcessorMessage(obj))
    }

    override fun printStackTrace(t: Throwable) {
        consumer.accept(FeedEvent.ItemProcessorError(t))
    }
}
