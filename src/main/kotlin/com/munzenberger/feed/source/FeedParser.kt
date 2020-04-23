package com.munzenberger.feed.source

import com.munzenberger.feed.Feed
import org.w3c.dom.Node

internal interface FeedParser {
    fun parse(node: Node): Feed
}
