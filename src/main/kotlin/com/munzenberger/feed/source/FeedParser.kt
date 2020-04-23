package com.munzenberger.feed.source

import com.munzenberger.feed.Feed
import org.w3c.dom.Node
import org.w3c.dom.NodeList

internal interface FeedParser {
    fun parse(node: Node): Feed
}

internal fun NodeList.asList(): List<Node> {
    var list = emptyList<Node>()
    for (i in 0 until length) {
        list = list + item(i)
    }
    return list
}
