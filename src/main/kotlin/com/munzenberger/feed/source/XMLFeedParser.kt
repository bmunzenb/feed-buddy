package com.munzenberger.feed.source

import com.munzenberger.feed.Feed
import org.w3c.dom.Node
import org.w3c.dom.NodeList
import java.lang.IllegalArgumentException
import javax.xml.xpath.XPathFactory

interface XMLFeedParser {
    fun parse(node: Node): Feed
}

object DynamicXMLFeedParser : XMLFeedParser {

    private val xPathFactory = XPathFactory.newInstance()

    private val parsers = mapOf(
            "rss" to RssXMLFeedParser(xPathFactory),
            "feed" to AtomXMLFeedParser(xPathFactory)
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
