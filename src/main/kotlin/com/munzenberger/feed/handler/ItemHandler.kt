package com.munzenberger.feed.handler

import com.munzenberger.feed.FeedContext
import com.munzenberger.feed.Item
import com.munzenberger.feed.status.FeedStatus
import java.util.function.Consumer

interface ItemHandler {
    fun execute(context: FeedContext, item: Item, statusConsumer: Consumer<FeedStatus>)
}
