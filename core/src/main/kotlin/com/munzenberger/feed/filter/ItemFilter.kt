package com.munzenberger.feed.filter

import com.munzenberger.feed.FeedContext
import com.munzenberger.feed.Item
import com.munzenberger.feed.ItemProcessorEvent
import com.munzenberger.feed.engine.ItemProcessor
import java.util.function.Consumer

fun interface ItemFilter : ItemProcessor {
    fun evaluate(
        context: FeedContext,
        item: Item,
        eventConsumer: Consumer<ItemProcessorEvent>,
    ): Boolean

    operator fun plus(other: ItemFilter): ItemFilter = CompositeItemFilter(listOf(this, other))
}

class CompositeItemFilter(
    private val filters: List<ItemFilter>,
) : ItemFilter {
    override fun evaluate(
        context: FeedContext,
        item: Item,
        eventConsumer: Consumer<ItemProcessorEvent>,
    ): Boolean = filters.all { it.evaluate(context, item, eventConsumer) }
}
