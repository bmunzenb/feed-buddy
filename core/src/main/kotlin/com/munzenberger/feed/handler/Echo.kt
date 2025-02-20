package com.munzenberger.feed.handler

import com.munzenberger.feed.FeedContext
import com.munzenberger.feed.Item
import com.munzenberger.feed.Logger

class Echo : ItemHandler {
    override fun execute(
        context: FeedContext,
        item: Item,
        logger: Logger,
    ) {
        logger.formatln(
            "[%s] %s",
            item.guid,
            item.title,
        )
    }
}
