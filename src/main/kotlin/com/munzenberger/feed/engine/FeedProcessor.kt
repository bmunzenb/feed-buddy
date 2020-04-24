package com.munzenberger.feed.engine

import com.munzenberger.feed.source.FeedSource
import java.time.LocalDateTime

class FeedProcessor(
        private val source: FeedSource,
        private val itemRegistry: ItemRegistry,
        private val itemProcessor: ItemProcessor
) {

    companion object {
        private val timestamp: String
            get() = LocalDateTime.now().toString()
    }

    fun execute() {
        try {

            print("$timestamp Reading from ${source.name} ... ")

            val feed = source.read()

            println("${feed.title}, ${feed.items.size} ${"item".pluralize(feed.items.size)}.")

            val processed = feed.items
                    .filterNot(itemRegistry::contains)
                    .filter(itemProcessor::execute)
                    .map(itemRegistry::add)
                    .count()

            println("$processed ${"item".pluralize(processed)} processed.")

        } catch (e: Throwable) {
            println("error [${e.javaClass.simpleName}] ${e.message}")
        }
    }
}

private fun String.pluralize(count: Int) = when (count) {
    1 -> this
    else -> this + "s"
}
