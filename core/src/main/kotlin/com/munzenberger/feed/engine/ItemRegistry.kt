package com.munzenberger.feed.engine

import com.munzenberger.feed.Item
import com.munzenberger.feed.config.FeedConfig

interface ItemRegistry {
    fun contains(item: Item): Boolean

    fun add(item: Item)
}

interface ItemRegistryFactory {
    fun getInstance(feedConfig: FeedConfig): ItemRegistry
}
