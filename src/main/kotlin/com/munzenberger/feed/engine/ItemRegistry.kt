package com.munzenberger.feed.engine

import com.munzenberger.feed.Item

interface ItemRegistry {
    fun contains(item: Item): Boolean
    fun add(item: Item)
}

class ItemRegistryLifecycleItemProcessor(
        private val registry: ItemRegistry,
        itemProcessor: ItemProcessor
) : LifecycleItemProcessor(itemProcessor) {

    override fun preExecute(item: Item): Boolean {
        return !registry.contains(item)
    }

    override fun postExecute(item: Item, processed: Boolean) {
        if (processed) {
            registry.add(item)
        }
    }
}
