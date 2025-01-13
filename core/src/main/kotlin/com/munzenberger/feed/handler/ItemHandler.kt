package com.munzenberger.feed.handler

import com.munzenberger.feed.FeedContext
import com.munzenberger.feed.Item
import com.munzenberger.feed.Logger
import com.munzenberger.feed.engine.ItemProcessor

interface ItemHandler : ItemProcessor {
    fun execute(context: FeedContext, item: Item, logger: Logger)

    operator fun plus(other: ItemHandler): ItemHandler = CompositeItemHandler(listOf(this, other))
}

class CompositeItemHandler(private val handlers: List<ItemHandler>) : ItemHandler {
    override fun execute(context: FeedContext, item: Item, logger: Logger) {
        handlers.forEach { it.execute(context, item, logger) }
    }
}
