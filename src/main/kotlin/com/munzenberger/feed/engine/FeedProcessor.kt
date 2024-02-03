package com.munzenberger.feed.engine

import com.munzenberger.feed.FeedContext
import com.munzenberger.feed.Logger
import com.munzenberger.feed.filter.ItemFilter
import com.munzenberger.feed.formatAsTime
import com.munzenberger.feed.handler.ItemHandler
import com.munzenberger.feed.source.FeedSource
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

class FeedProcessor(
        private val source: FeedSource,
        private val itemRegistry: ItemRegistry,
        private val itemFilter: ItemFilter,
        private val itemHandler: ItemHandler,
        private val logger: Logger
) {

    companion object {
        private val tf = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT)
        private val timestamp: String
            get() = tf.format(LocalDateTime.now())
    }

    fun execute() {
        try {

            logger.print("[$timestamp] Reading ${source.name}... ")

            val startTime = System.currentTimeMillis()

            val feed = source.read()

            logger.formatln(
                "%s, %d %s.",
                feed.title,
                feed.items.size,
                "item".pluralize(feed.items.size)
            )

            val context = FeedContext(source.name, feed.title)

            var processed = 0
            var errors = 0

            val items = feed.items
                    .filterNot(itemRegistry::contains)
                    .filter { itemFilter.evaluate(context, it, logger) }

            items.forEachIndexed { index, item ->
                logger.formatln(
                    "[%d/%d] Processing \"%s\" (%s)...",
                    index+1,
                    items.size,
                    item.title,
                    item.guid
                )

                try {
                    itemHandler.execute(context, item, logger)
                    itemRegistry.add(item)
                    processed++
                } catch (e: Throwable) {
                    logger.println("${e.javaClass.simpleName}: ${e.message}")
                    logger.printStackTrace(e)
                    errors++
                }
            }

            if (items.isNotEmpty()) {
                val elapsed = System.currentTimeMillis() - startTime
                when (errors) {
                    0 -> logger.formatln(
                        "%d %s processed in %s.",
                        processed,
                        "item".pluralize(processed),
                        elapsed.formatAsTime()
                    )
                    else -> logger.formatln(
                        "%d %s processed successfully, %d %s in %s.",
                        processed,
                        "item".pluralize(processed),
                        errors,
                        "failure".pluralize(errors),
                        elapsed.formatAsTime()
                    )
                }
            }

        } catch (e: Throwable) {
            logger.println("${e.javaClass.simpleName}: ${e.message}")
            logger.printStackTrace(e)
        }
    }
}

fun String.pluralize(count: Int) = when (count) {
    1 -> this
    else -> this + "s"
}
