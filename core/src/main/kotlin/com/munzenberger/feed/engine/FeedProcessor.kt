package com.munzenberger.feed.engine

import com.munzenberger.feed.FeedContext
import com.munzenberger.feed.FeedEvent
import com.munzenberger.feed.ItemProcessorEvent
import com.munzenberger.feed.SystemEvent
import com.munzenberger.feed.filter.ItemFilter
import com.munzenberger.feed.handler.ItemHandler
import com.munzenberger.feed.source.FeedSource
import java.util.function.Consumer

class FeedProcessor(
    private val source: FeedSource,
    private val itemRegistry: ItemRegistry,
    private val itemFilter: ItemFilter,
    private val itemHandler: ItemHandler,
    private val eventConsumer: Consumer<FeedEvent>,
) : Runnable {
    override fun run() {
        try {
            eventConsumer.accept(SystemEvent.ProcessorFeedStart(source.name))

            val feed = source.read()

            eventConsumer.accept(SystemEvent.ProcessorFeedRead(feed.title, feed.items.size))

            val context = FeedContext(source.name, feed.title)

            val itemProcessorEventConsumer = ItemProcessorEventConsumer(eventConsumer)

            val items =
                feed.items
                    .filterNot(itemRegistry::contains)
                    .filter { itemFilter.evaluate(context, it, itemProcessorEventConsumer) }

            eventConsumer.accept(SystemEvent.ProcessorFeedFilter(items.size))

            items.forEach { item ->
                try {
                    eventConsumer.accept(SystemEvent.ProcessorItemStart(item.title, item.guid))
                    itemHandler.execute(context, item, itemProcessorEventConsumer)
                    itemRegistry.add(item)
                } catch (e: Throwable) {
                    eventConsumer.accept(SystemEvent.ProcessorItemError(e))
                } finally {
                    eventConsumer.accept(SystemEvent.ProcessorItemComplete)
                }
            }
        } catch (e: Throwable) {
            eventConsumer.accept(SystemEvent.ProcessorFeedError(e))
        } finally {
            eventConsumer.accept(SystemEvent.ProcessorFeedComplete)
        }
    }
}

private class ItemProcessorEventConsumer(
    private val consumer: Consumer<FeedEvent>,
) : Consumer<ItemProcessorEvent> {
    override fun accept(event: ItemProcessorEvent) {
        consumer.accept(event)
    }
}
