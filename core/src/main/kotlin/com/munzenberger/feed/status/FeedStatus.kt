package com.munzenberger.feed.status

sealed class FeedStatus {
    data class OperatorStart(
        val feedCount: Int,
        val configProviderName: String,
    ) : FeedStatus()

    data object OperatorConfigurationChange : FeedStatus()

    data class ProcessorFeedStart(
        val sourceName: String,
    ) : FeedStatus()

    data class ProcessorFeedRead(
        val feedTitle: String,
        val itemCount: Int,
    ) : FeedStatus()

    data class ProcessorFeedFilter(
        val itemCount: Int,
    ) : FeedStatus()

    data class ProcessorItemStart(
        val itemTitle: String,
        val itemGuid: String,
    ) : FeedStatus()

    data object ProcessorItemComplete : FeedStatus()

    data class ProcessorItemError(
        val error: Throwable,
    ) : FeedStatus()

    data class ProcessorFeedError(
        val error: Throwable,
    ) : FeedStatus()

    data object ProcessorFeedComplete : FeedStatus()

    data class ItemProcessorMessage(
        val message: Any,
        val isPartialMessage: Boolean = false,
    ) : FeedStatus()

    data class ItemProcessorError(
        val error: Throwable,
    ) : FeedStatus()
}
