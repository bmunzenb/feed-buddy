package com.munzenberger.feed.app

import com.munzenberger.feed.FeedEvent
import com.munzenberger.feed.ItemProcessorEvent
import com.munzenberger.feed.SystemEvent
import com.munzenberger.feed.formatAsTime
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.function.Consumer

class LoggingEventConsumer(
    private val logger: Logger,
) : Consumer<FeedEvent> {
    companion object {
        private val tf = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT)
        private val timestamp: String
            get() = tf.format(LocalDateTime.now())
    }

    private fun String.pluralize(count: Int) =
        when (count) {
            1 -> this
            else -> this + "s"
        }

    // statistics
    private var startTime = 0L
    private var count = 0
    private var processed = 0
    private var errors = 0

    @Suppress("LongMethod", "CyclomaticComplexMethod")
    override fun accept(event: FeedEvent) {
        when (event) {
            is SystemEvent.OperatorStart ->
                logger.formatln(
                    "Scheduling %,d %s from %s.",
                    event.feedCount,
                    "feed".pluralize(event.feedCount),
                    event.configProviderName,
                )

            is SystemEvent.OperatorConfigurationChange ->
                logger.println(
                    "Detected configuration change.",
                )

            is SystemEvent.ProcessorFeedStart -> {
                startTime = System.currentTimeMillis()
                count = 0
                processed = 0
                errors = 0

                logger.format(
                    "[%s] Reading %s... ",
                    timestamp,
                    event.sourceName,
                )
            }

            is SystemEvent.ProcessorFeedRead ->
                logger.formatln(
                    "%s, %,d %s.",
                    event.feedTitle,
                    event.itemCount,
                    "item".pluralize(event.itemCount),
                )

            is SystemEvent.ProcessorFeedFilter -> {
                count = event.itemCount
            }

            is SystemEvent.ProcessorItemStart -> {
                processed++
                logger.formatln(
                    "[%,d/%,d] Processing \"%s\" (%s)...",
                    processed,
                    count,
                    event.itemTitle,
                    event.itemGuid,
                )
            }

            is SystemEvent.ProcessorItemError -> {
                errors++
                onError(event.error)
            }

            is SystemEvent.ProcessorItemComplete -> Unit

            is SystemEvent.ProcessorFeedError -> onError(event.error)

            is SystemEvent.ProcessorFeedComplete -> {
                val elapsed = System.currentTimeMillis() - startTime
                if (count > 0) {
                    when (errors) {
                        0 ->
                            logger.formatln(
                                "%,d %s processed in %s.",
                                processed,
                                "item".pluralize(processed),
                                elapsed.formatAsTime(),
                            )
                        else ->
                            logger.formatln(
                                "%,d %s processed successfully, %,d %s in %s.",
                                processed - errors,
                                "item".pluralize(processed),
                                errors,
                                "failure".pluralize(errors),
                                elapsed.formatAsTime(),
                            )
                    }
                }
            }

            is ItemProcessorEvent.Message -> {
                if (event.isPartialMessage) {
                    logger.print(event.message)
                } else {
                    logger.println(event.message)
                }
            }

            is ItemProcessorEvent.Error -> onError(event.error)
        }
    }

    private fun onError(error: Throwable) {
        logger.println("${error.javaClass.simpleName}: ${error.message}")
        logger.printStackTrace(error)
    }
}
