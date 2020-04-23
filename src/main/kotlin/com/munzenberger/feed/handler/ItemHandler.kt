package com.munzenberger.feed.handler

import com.munzenberger.feed.Item

interface ItemHandler {
    fun execute(item: Item)
}
