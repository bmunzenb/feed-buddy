package com.munzenberger.feed.engine

import com.munzenberger.feed.source.FeedSource
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

class FeedProcessor(
        private val source: FeedSource,
        private val itemRegistry: ItemRegistry,
        private val itemProcessor: ItemProcessor
) {

    companion object {
        private val tf = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT)
        private val timestamp: String
            get() = tf.format(LocalDateTime.now())
    }

    fun execute() {
        try {

            print("$timestamp Reading from ${source.name}... ")

            val feed = source.read()

            println("${feed.title}, ${feed.items.size} ${"item".pluralize(feed.items.size)}.")

            var processed = 0

            feed.items.filterNot(itemRegistry::contains).forEach {
                if (itemProcessor.execute(it)) {
                    itemRegistry.add(it)
                    processed++
                }
            }

            if (processed > 0) {
                println("$processed ${"item".pluralize(processed)} processed.")
            }

        } catch (e: Throwable) {
            println("error [${e.javaClass.simpleName}] ${e.message}")
        }
    }
}

private fun String.pluralize(count: Int) = when (count) {
    1 -> this
    else -> this + "s"
}
