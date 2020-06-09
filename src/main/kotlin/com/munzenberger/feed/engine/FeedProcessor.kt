package com.munzenberger.feed.engine

import com.munzenberger.feed.FeedContext
import com.munzenberger.feed.filter.ItemFilter
import com.munzenberger.feed.handler.ItemHandler
import com.munzenberger.feed.source.FeedSource
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

class FeedProcessor(
        private val source: FeedSource,
        private val itemRegistry: ItemRegistry,
        private val itemFilter: ItemFilter,
        private val itemHandler: ItemHandler
) {

    companion object {
        private val tf = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT)
        private val timestamp: String
            get() = tf.format(LocalDateTime.now())
    }

    fun execute() {
        try {

            print("$timestamp Reading ${source.name}... ")

            val feed = source.read()

            with(feed.items.size) {
                println("${feed.title}, $this ${"item".pluralize(this)}.")
            }

            val context = FeedContext(source.name, feed.title)

            var processed = 0
            var errors = 0

            val items = feed.items
                    .filterNot(itemRegistry::contains)
                    .filter { itemFilter.evaluate(context, it) }

            items.forEachIndexed { index, item ->
                println("--> Processing item ${index+1} of ${items.size}, \"${item.title}\" (${item.guid})")
                try {
                    itemHandler.execute(context, item)
                    itemRegistry.add(item)
                    processed++
                } catch (e: Throwable) {
                    println("error [${e.javaClass.simpleName}] ${e.message}")
                    e.printStackTrace()
                    errors++
                }
            }

            if (items.isNotEmpty()) {
                when (errors) {
                    0 -> println("$processed ${"item".pluralize(processed)} processed.")
                    else -> println("$processed ${"item".pluralize(processed)} processed successfully, $errors ${"failure".pluralize(errors)}.")
                }
            }

        } catch (e: Throwable) {
            println("error [${e.javaClass.simpleName}] ${e.message}")
            e.printStackTrace()
        }
    }
}

fun String.pluralize(count: Int) = when (count) {
    1 -> this
    else -> this + "s"
}
