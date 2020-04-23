package com.munzenberger.feed.source

import com.munzenberger.feed.Feed
import org.xml.sax.InputSource
import java.net.URL
import javax.xml.parsers.DocumentBuilderFactory

class URLFeedSource(override val name: String, private val source: URL) : FeedSource {

    private val dbFactory = DocumentBuilderFactory.newInstance()

    override fun read(): Feed {

        // handles many of the things that can go wrong while reading XML streams
        val response = URLClient.connect(source)
        val reader = XMLInputStreamDecoder.decode(response.inStream, response.encoding)
        val inputSource = InputSource(reader)
        val document = dbFactory.newDocumentBuilder().parse(inputSource)

        val root = document.documentElement

        val parser = when (val type = root.nodeName) {
            "rss" -> RssDocumentParser()
            "feed" -> AtomDocumentParser()
            else -> throw IllegalArgumentException("Unsupported feed type: $type")
        }

        return parser.parse(root)
    }
}
