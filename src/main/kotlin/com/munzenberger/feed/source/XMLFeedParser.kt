package com.munzenberger.feed.source

import com.munzenberger.feed.Feed
import org.w3c.dom.Node
import org.w3c.dom.NodeList
import java.lang.IllegalArgumentException

interface XMLFeedParser {
    fun parse(node: Node): Feed
}

class DynamicXMLFeedParser : XMLFeedParser {

    private val parsers = mapOf(
            "rss" to RssXMLFeedParser(),
            "feed" to AtomXMLFeedParser()
    )

    override fun parse(node: Node): Feed {

        val type = node.nodeName

        return when (val parser = parsers[type]) {
            null -> throw IllegalArgumentException("Unsupported feed type: $type")
            else -> parser.parse(node)
        }
    }
}

internal fun NodeList.asList(): List<Node> {
    var list = emptyList<Node>()
    for (i in 0 until length) {
        list = list + item(i)
    }
    return list
}
