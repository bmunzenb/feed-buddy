package com.munzenberger.feed.source

import com.munzenberger.feed.Feed
import com.munzenberger.feed.URLClient
import org.xml.sax.InputSource
import java.net.URL
import javax.xml.parsers.DocumentBuilderFactory

class XMLFeedSource(
        private val source: URL,
        private val userAgent: String? = null,
        private val parser: XMLFeedParser = DynamicXMLFeedParser,
        private val documentBuilderFactory: DocumentBuilderFactory = DocumentBuilderFactory.newInstance()
) : FeedSource {

    override val name: String = source.toExternalForm()

    override fun read(): Feed {

        val response = URLClient.connect(source, userAgent)

        val reader = XMLReader(response.inStream, response.encoding)

        val inputSource = InputSource(reader)

        val document = documentBuilderFactory.newDocumentBuilder().parse(inputSource)

        val root = document.documentElement

        return parser.parse(root)
    }
}
