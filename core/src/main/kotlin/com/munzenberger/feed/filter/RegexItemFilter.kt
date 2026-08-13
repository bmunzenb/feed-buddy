package com.munzenberger.feed.filter

import com.munzenberger.feed.FeedContext
import com.munzenberger.feed.Item
import com.munzenberger.feed.ItemProcessorEvent
import java.util.function.Consumer

class RegexItemFilter : ItemFilter {
    var title: String? = null
        set(value) {
            field = value
            titleRegex = value?.let(::Regex)
        }

    var content: String? = null
        set(value) {
            field = value
            contentRegex = value?.let(::Regex)
        }

    var category: String? = null
        set(value) {
            field = value
            categoryRegex = value?.let(::Regex)
        }

    private var titleRegex: Regex? = null
    private var contentRegex: Regex? = null
    private var categoryRegex: Regex? = null

    override fun evaluate(
        context: FeedContext,
        item: Item,
        eventConsumer: Consumer<ItemProcessorEvent>,
    ): Boolean {
        val matchers =
            listOf(
                titleRegex?.matches(item.title) ?: true,
                contentRegex?.matches(item.content) ?: true,
                categoryRegex?.let { r -> item.categories.any { c -> r.matches(c) } } ?: true,
            )

        return matchers.all { it }
    }
}
