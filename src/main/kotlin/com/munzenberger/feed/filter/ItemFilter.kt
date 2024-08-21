package com.munzenberger.feed.filter

import com.munzenberger.feed.FeedContext
import com.munzenberger.feed.Item
import com.munzenberger.feed.Logger
import com.munzenberger.feed.engine.ItemProcessor

interface ItemFilter : ItemProcessor {
    fun evaluate(context: FeedContext, item: Item, logger: Logger): Boolean
}
