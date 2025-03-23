package com.munzenberger.feed.handler

import com.munzenberger.feed.FeedContext
import com.munzenberger.feed.Item
import com.munzenberger.feed.ItemProcessorEvent
import com.munzenberger.feed.formatln
import java.util.function.Consumer

class Echo : ItemHandler {
    override fun execute(
        context: FeedContext,
        item: Item,
        eventConsumer: Consumer<ItemProcessorEvent>,
    ) {
        eventConsumer.formatln(
            "[%s] %s",
            item.guid,
            item.title,
        )
    }
}
