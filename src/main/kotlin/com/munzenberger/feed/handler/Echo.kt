package com.munzenberger.feed.handler

import com.munzenberger.feed.Item

class Echo : ItemHandler {

    override fun execute(item: Item) {
        println(item)
    }
}
