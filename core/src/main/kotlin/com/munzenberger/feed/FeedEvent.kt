package com.munzenberger.feed

sealed class FeedEvent {
    data class OperatorStart(
        val feedCount: Int,
        val configProviderName: String,
    ) : FeedEvent()

    data object OperatorConfigurationChange : FeedEvent()

    data class ProcessorFeedStart(
        val sourceName: String,
    ) : FeedEvent()

    data class ProcessorFeedRead(
        val feedTitle: String,
        val itemCount: Int,
    ) : FeedEvent()

    data class ProcessorFeedFilter(
        val itemCount: Int,
    ) : FeedEvent()

    data class ProcessorItemStart(
        val itemTitle: String,
        val itemGuid: String,
    ) : FeedEvent()

    data object ProcessorItemComplete : FeedEvent()

    data class ProcessorItemError(
        val error: Throwable,
    ) : FeedEvent()

    data class ProcessorFeedError(
        val error: Throwable,
    ) : FeedEvent()

    data object ProcessorFeedComplete : FeedEvent()

    data class ItemProcessorMessage(
        val message: Any,
        val isPartialMessage: Boolean = false,
    ) : FeedEvent()

    data class ItemProcessorError(
        val error: Throwable,
    ) : FeedEvent()
}
