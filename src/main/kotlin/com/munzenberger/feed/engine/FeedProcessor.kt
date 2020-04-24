package com.munzenberger.feed.engine

import com.munzenberger.feed.source.FeedSource
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

class FeedProcessor(
        private val source: FeedSource,
        private val itemProcessor: ItemProcessor
) {

    companion object {
        private val tf = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.LONG)
        private val timestamp: String
            get() = tf.format(LocalDateTime.now())
    }

    fun execute() {
        try {

            print("$timestamp Reading from ${source.name} ... ")

            val feed = source.read()

            println("${feed.title}, ${feed.items.size} item(s).")

            val processed = feed.items
                    .map { itemProcessor.execute(it) }
                    .count { it }

            println("Processed $processed item(s).")

        } catch (e: Throwable) {
            println("error [${e.javaClass.simpleName}] ${e.message}")
        }
    }
}
