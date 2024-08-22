package com.munzenberger.feed.filter

import com.munzenberger.feed.FeedContext
import com.munzenberger.feed.Item
import com.munzenberger.feed.Logger

class RegexItemFilter : ItemFilter {

    var title: String? = null
    var content: String? = null

    override fun evaluate(context: FeedContext, item: Item, logger: Logger): Boolean {

        val matchers = listOf(
                title?.let { Regex(it).matches(item.title) } ?: true,
                content?.let { Regex(it).matches(item.content) } ?: true
        )

        return matchers.all { it }
    }
}
