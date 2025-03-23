package com.munzenberger.feed

import java.util.function.Consumer

interface FeedEvent

sealed class SystemEvent : FeedEvent {
    data class OperatorStart(
        val feedCount: Int,
        val configProviderName: String,
    ) : SystemEvent()

    data object OperatorConfigurationChange : SystemEvent()

    data class ProcessorFeedStart(
        val sourceName: String,
    ) : SystemEvent()

    data class ProcessorFeedRead(
        val feedTitle: String,
        val itemCount: Int,
    ) : SystemEvent()

    data class ProcessorFeedFilter(
        val itemCount: Int,
    ) : SystemEvent()

    data class ProcessorItemStart(
        val itemTitle: String,
        val itemGuid: String,
    ) : SystemEvent()

    data object ProcessorItemComplete : SystemEvent()

    data class ProcessorItemError(
        val error: Throwable,
    ) : SystemEvent()

    data class ProcessorFeedError(
        val error: Throwable,
    ) : SystemEvent()

    data object ProcessorFeedComplete : SystemEvent()
}

interface ItemProcessorEvent : FeedEvent {
    data class Message(
        val message: Any,
        val isPartialMessage: Boolean = false,
    ) : ItemProcessorEvent

    data class Error(
        val error: Throwable,
    ) : ItemProcessorEvent
}

fun Consumer<ItemProcessorEvent>.print(obj: Any) {
    accept(ItemProcessorEvent.Message(obj.toString(), isPartialMessage = true))
}

fun Consumer<ItemProcessorEvent>.println(obj: Any) {
    accept(ItemProcessorEvent.Message(obj.toString()))
}

fun Consumer<ItemProcessorEvent>.format(
    format: String,
    vararg args: Any,
) {
    val message = String.format(format, *args)
    print(message)
}

fun Consumer<ItemProcessorEvent>.formatln(
    format: String,
    vararg args: Any,
) {
    val message = String.format(format, *args)
    println(message)
}
