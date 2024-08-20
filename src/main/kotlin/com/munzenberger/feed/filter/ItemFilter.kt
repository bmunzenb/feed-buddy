package com.munzenberger.feed.filter

import com.munzenberger.feed.FeedContext
import com.munzenberger.feed.Item
import com.munzenberger.feed.status.FeedStatus
import java.util.function.Consumer

interface ItemFilter {
    fun evaluate(context: FeedContext, item: Item, statusConsumer: Consumer<FeedStatus>): Boolean
}
