package com.munzenberger.feed.engine

import com.munzenberger.feed.Item

interface ItemProcessor {
    fun execute(item: Item): Boolean
}

abstract class LifecycleItemProcessor(private val itemProcessor: ItemProcessor) : ItemProcessor {

    open fun preExecute(item: Item): Boolean = true

    open fun postExecute(item: Item, processed: Boolean) {}

    override fun execute(item: Item): Boolean {

        val processed = when (preExecute(item)) {
            true -> itemProcessor.execute(item)
            else -> false
        }

        postExecute(item, processed)

        return processed
    }
}
