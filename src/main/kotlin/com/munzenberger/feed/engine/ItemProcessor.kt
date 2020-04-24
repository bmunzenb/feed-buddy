package com.munzenberger.feed.engine

import com.munzenberger.feed.Item
import com.munzenberger.feed.handler.ItemHandler

interface ItemProcessor {
    fun execute(item: Item): Boolean
}

class HandlersItemProcessor(private val handlers: List<ItemHandler>) : ItemProcessor {

    private fun ItemHandler.executeWithTryCatch(item: Item): Boolean {
        return try {
            execute(item)
            true
        } catch (e: Throwable) {
            println("error [${e.javaClass.simpleName}] ${e.message}")
            false
        }
    }

    override fun execute(item: Item): Boolean {
        return handlers.all { it.executeWithTryCatch(item) }
    }
}
