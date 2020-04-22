package com.munzenberger.feed.source

import com.munzenberger.feed.Feed
import java.net.URL
import javax.xml.parsers.DocumentBuilderFactory

class URLFeedSource(override val name: String, private val source: URL) : FeedSource {

    private val dbFactory = DocumentBuilderFactory.newInstance()

    override fun read(): Feed {

        val builder = dbFactory.newDocumentBuilder()

        val inStream = source.openStream()
        val document = builder.parse(inStream)

        val parser = when (val type = document.firstChild.nodeName) {
            "rss" -> RssDocumentParser()
            "feed" -> AtomDocumentParser()
            else -> throw IllegalArgumentException("Unsupported feed type: $type")
        }

        return parser.parse(document)
    }
}
