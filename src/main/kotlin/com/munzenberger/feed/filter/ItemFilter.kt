package com.munzenberger.feed.filter

import com.munzenberger.feed.FeedContext
import com.munzenberger.feed.Item
import com.munzenberger.feed.Logger

interface ItemFilter {
    fun evaluate(context: FeedContext, item: Item, logger: Logger): Boolean
}
