package com.munzenberger.feed.handler

import com.munzenberger.feed.FeedContext
import com.munzenberger.feed.Item

class Echo : ItemHandler {

    override fun execute(context: FeedContext, item: Item) {
        println(item)
    }
}
