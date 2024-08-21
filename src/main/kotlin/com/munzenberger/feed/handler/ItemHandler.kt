package com.munzenberger.feed.handler

import com.munzenberger.feed.FeedContext
import com.munzenberger.feed.Item
import com.munzenberger.feed.Logger

interface ItemHandler {
    fun execute(context: FeedContext, item: Item, logger: Logger)
}
