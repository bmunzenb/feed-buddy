package com.munzenberger.feed.engine

import com.munzenberger.feed.Item

interface ItemRegistry {
    fun contains(item: Item): Boolean
    fun add(item: Item)
}
