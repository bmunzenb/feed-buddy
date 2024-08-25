package com.munzenberger.feed.engine

import com.munzenberger.feed.Item
import java.net.URL

interface ItemRegistry {
    fun contains(item: Item): Boolean
    fun add(item: Item)
}

interface ItemRegistryFactory {
    fun getInstance(url: URL): ItemRegistry
}
