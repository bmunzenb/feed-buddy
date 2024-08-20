package com.munzenberger.feed.status

import com.munzenberger.feed.Logger
import com.munzenberger.feed.formatAsTime
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.function.Consumer

class LoggingStatusConsumer(private val logger: Logger) : Consumer<FeedStatus> {

    companion object {
        private val tf = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT)
        private val timestamp: String
            get() = tf.format(LocalDateTime.now())
    }

    private fun String.pluralize(count: Int) = when (count) {
        1 -> this
        else -> this + "s"
    }

    override fun accept(status: FeedStatus) {
        when (status) {

            is FeedStatus.OperatorStart -> logger.formatln(
                "Scheduling %d %s from %s.",
                status.feedCount,
                "feed".pluralize(status.feedCount),
                status.configProviderName
            )

            is FeedStatus.OperatorConfigurationChange -> logger.println(
                "Detected configuration change."
            )

            is FeedStatus.ProcessorFeedStart -> logger.format(
                "[%s] Reading %s... ",
                timestamp,
                status.sourceName
            )

            is FeedStatus.ProcessorFeedRead -> logger.formatln(
                "%s, %d %s.",
                status.feedTitle,
                status.itemCount,
                "item".pluralize(status.itemCount)
            )

            is FeedStatus.ProcessorItemStart -> logger.formatln(
                "[%d/%d] Processing \"%s\" (%s)...",
                status.itemIndex+1,
                status.itemCount,
                status.itemTitle,
                status.itemGuid
            )

            is FeedStatus.ProcessorItemError -> {
                logger.println("${status.error.javaClass.simpleName}: ${status.error.message}")
                logger.printStackTrace(status.error)
            }

            is FeedStatus.ProcessorFeedError -> {
                logger.println("${status.error.javaClass.simpleName}: ${status.error.message}")
                logger.printStackTrace(status.error)
            }

            is FeedStatus.ProcessorFeedComplete -> {
                if (status.itemCount > 0) {
                    when (status.errors) {
                        0 -> logger.formatln(
                            "%d %s processed in %s.",
                            status.itemsProcessed,
                            "item".pluralize(status.itemsProcessed),
                            status.elapsedTime.formatAsTime()
                        )
                        else -> logger.formatln(
                            "%d %s processed successfully, %d %s in %s.",
                            status.itemsProcessed,
                            "item".pluralize(status.itemsProcessed),
                            status.errors,
                            "failure".pluralize(status.errors),
                            status.elapsedTime.formatAsTime()
                        )
                    }
                }
            }

            is FeedStatus.HandlerMessage -> {
                if (status.isPartialMessage) {
                    logger.print(status.message)
                } else {
                    logger.println(status.message)
                }
            }
        }
    }
}
