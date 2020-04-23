package com.munzenberger.feed.source

import com.munzenberger.feed.Feed
import org.xml.sax.InputSource
import java.net.URL
import javax.xml.parsers.DocumentBuilderFactory

class URLFeedSource(private val source: URL) : FeedSource {

    override fun read(): Feed {

        // handles many of the things that can go wrong while reading XML streams
        val response = URLClient.connect(source)
        val reader = XMLInputStreamDecoder.decode(response.inStream, response.encoding)
        val inputSource = InputSource(reader)

        val document = DocumentBuilderFactory.newInstance()
                .newDocumentBuilder()
                .parse(inputSource)

        val root = document.documentElement

        val parser = when (val type = root.nodeName) {
            "rss" -> RssFeedParser()
            "feed" -> AtomFeedParser()
            else -> throw IllegalArgumentException("Unsupported feed type: $type")
        }

        return parser.parse(root)
    }
}
