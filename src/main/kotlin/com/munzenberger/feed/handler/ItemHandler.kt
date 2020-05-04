package com.munzenberger.feed.handler

import com.munzenberger.feed.FeedContext
import com.munzenberger.feed.Item

interface ItemHandler {
    fun execute(context: FeedContext, item: Item)
}
