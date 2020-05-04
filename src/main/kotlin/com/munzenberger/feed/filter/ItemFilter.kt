package com.munzenberger.feed.filter

import com.munzenberger.feed.FeedContext
import com.munzenberger.feed.Item

interface ItemFilter {
    fun evaluate(context: FeedContext, item: Item): Boolean
}
