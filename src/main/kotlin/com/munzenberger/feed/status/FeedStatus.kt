package com.munzenberger.feed.status

sealed class FeedStatus {

    data class OperatorStart(
        val feedCount: Int,
        val configProviderName: String
    ) : FeedStatus()

    data object OperatorConfigurationChange : FeedStatus()

    data class ProcessorFeedStart(
        val sourceName: String
    ) : FeedStatus()

    data class ProcessorFeedRead(
        val feedTitle: String,
        val itemCount: Int
    ) : FeedStatus()

    data class ProcessorItemStart(
        val itemIndex: Int,
        val itemCount: Int,
        val itemTitle: String,
        val itemGuid: String
    ) : FeedStatus()

    data class ProcessorItemError(
        val error: Throwable
    ) : FeedStatus()

    data class ProcessorFeedError(
        val error: Throwable
    ) : FeedStatus()

    data class ProcessorFeedComplete(
        val itemCount: Int,
        val itemsProcessed: Int,
        val errors: Int,
        val elapsedTime: Long
    ) : FeedStatus()

    data class HandlerMessage(
        val message: Any,
        val isPartialMessage: Boolean = false
    ) : FeedStatus()

    data class HandlerError(
        val error: Throwable
    ) : FeedStatus()
}
