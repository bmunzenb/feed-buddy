package com.munzenberger.feed.source

import com.ctc.wstx.stax.WstxInputFactory
import com.munzenberger.feed.Feed
import com.munzenberger.feed.URLClient
import java.net.URL
import javax.xml.stream.XMLEventReader
import javax.xml.stream.events.StartElement

class XMLFeedSource(
        private val source: URL,
        private val userAgent: String? = null
) : FeedSource {

    private val factory = WstxInputFactory.newFactory()

    override val name: String = source.toExternalForm()

    override fun read(): Feed {

        val response = URLClient.connect(source, userAgent)

        val reader = XMLFilterReader(response.inStream, response.encoding)

        val eventReader = factory.createXMLEventReader(reader)

        // inspect the first element to determine the feed type
        val firstElement = firstElement(eventReader)

        val parser = when (val type = firstElement.name.localPart) {
            "rss" -> RssXMLFeedParser
            "feed" -> AtomXMLFeedParser
            else -> throw IllegalArgumentException("No parser available for feed type '$type'")
        }

        return parser.parse(eventReader)
    }

    private fun firstElement(eventReader: XMLEventReader): StartElement {
        while (eventReader.hasNext()) {
            val event = eventReader.nextEvent()
            if (event.isStartElement) {
                return event.asStartElement()
            }
        }
        throw IllegalStateException("End of document before start element.")
    }
}
