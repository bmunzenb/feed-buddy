package com.munzenberger.feed.handler

import com.munzenberger.feed.FeedContext
import com.munzenberger.feed.Item
import com.munzenberger.feed.ItemProcessorEvent
import com.munzenberger.feed.engine.ItemProcessor
import java.util.function.Consumer

fun interface ItemHandler : ItemProcessor {
    fun execute(
        context: FeedContext,
        item: Item,
        eventConsumer: Consumer<ItemProcessorEvent>,
    )

    operator fun plus(other: ItemHandler): ItemHandler = CompositeItemHandler(listOf(this, other))
}

class CompositeItemHandler(
    private val handlers: List<ItemHandler>,
) : ItemHandler {
    override fun execute(
        context: FeedContext,
        item: Item,
        eventConsumer: Consumer<ItemProcessorEvent>,
    ) {
        handlers.forEach { it.execute(context, item, eventConsumer) }
    }
}
