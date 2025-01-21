package com.munzenberger.feed.app

import com.munzenberger.feed.Logger
import com.munzenberger.feed.formatAsTime
import com.munzenberger.feed.status.FeedStatus
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.function.Consumer

class LoggingStatusConsumer(
    private val logger: Logger,
) : Consumer<FeedStatus> {
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
    override fun accept(status: FeedStatus) {
        when (status) {
            is FeedStatus.OperatorStart ->
                logger.formatln(
                    "Scheduling %d %s from %s.",
                    status.feedCount,
                    "feed".pluralize(status.feedCount),
                    status.configProviderName,
                )

            is FeedStatus.OperatorConfigurationChange ->
                logger.println(
                    "Detected configuration change.",
                )

            is FeedStatus.ProcessorFeedStart -> {
                startTime = System.currentTimeMillis()
                count = 0
                processed = 0
                errors = 0

                logger.format(
                    "[%s] Reading %s... ",
                    timestamp,
                    status.sourceName,
                )
            }

            is FeedStatus.ProcessorFeedRead ->
                logger.formatln(
                    "%s, %d %s.",
                    status.feedTitle,
                    status.itemCount,
                    "item".pluralize(status.itemCount),
                )

            is FeedStatus.ProcessorFeedFilter -> {
                count = status.itemCount
            }

            is FeedStatus.ProcessorItemStart -> {
                processed++
                logger.formatln(
                    "[%d/%d] Processing \"%s\" (%s)...",
                    processed,
                    count,
                    status.itemTitle,
                    status.itemGuid,
                )
            }

            is FeedStatus.ProcessorItemError -> {
                errors++
                onError(status.error)
            }

            is FeedStatus.ProcessorItemComplete -> Unit

            is FeedStatus.ProcessorFeedError -> onError(status.error)

            is FeedStatus.ProcessorFeedComplete -> {
                val elapsed = System.currentTimeMillis() - startTime
                if (count > 0) {
                    when (errors) {
                        0 ->
                            logger.formatln(
                                "%d %s processed in %s.",
                                processed,
                                "item".pluralize(processed),
                                elapsed.formatAsTime(),
                            )
                        else ->
                            logger.formatln(
                                "%d %s processed successfully, %d %s in %s.",
                                processed - errors,
                                "item".pluralize(processed),
                                errors,
                                "failure".pluralize(errors),
                                elapsed.formatAsTime(),
                            )
                    }
                }
            }

            is FeedStatus.ItemProcessorMessage -> {
                if (status.isPartialMessage) {
                    logger.print(status.message)
                } else {
                    logger.println(status.message)
                }
            }

            is FeedStatus.ItemProcessorError -> onError(status.error)
        }
    }

    private fun onError(error: Throwable) {
        logger.println("${error.javaClass.simpleName}: ${error.message}")
        logger.printStackTrace(error)
    }
}
