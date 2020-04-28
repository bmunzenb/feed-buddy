package com.munzenberger.feed.source

import com.munzenberger.feed.Enclosure
import com.munzenberger.feed.Feed
import com.munzenberger.feed.Item
import javax.xml.stream.XMLEventReader
import javax.xml.stream.events.Attribute
import javax.xml.stream.events.StartElement

private class RssChannel {
    var title = ""
    val items = mutableListOf<RssItem>()
}

private class RssItem {
    var title = ""
    var description = ""
    var link = ""
    var guid = ""
    var pubDate = ""
    val enclosures = mutableListOf<RssEnclosure>()
}

private class RssEnclosure {
    var url = ""
}

private fun RssChannel.toFeed() = Feed(
        title = title,
        items = items.map { it.toItem() }
)

private fun RssItem.toItem() = Item(
        title = title,
        content = description,
        link = link,
        guid = guid,
        timestamp = pubDate,
        enclosures = enclosures.map { it.toEnclosure() }
)

private fun RssEnclosure.toEnclosure() = Enclosure(url)

internal object RssXMLFeedParser : XMLFeedParser {

    private const val CHANNEL = "channel"
    private const val TITLE = "title"
    private const val ITEM = "item"
    private const val DESCRIPTION = "description"
    private const val LINK = "link"
    private const val GUID = "guid"
    private const val PUBDATE = "pubDate"
    private const val ENCLOSURE = "enclosure"
    private const val URL = "url"

    override fun parse(eventReader: XMLEventReader): Feed {

        val channel = RssChannel()

        while (eventReader.hasNext()) {
            val event = eventReader.nextEvent()

            if (event.isStartElement) {
                when (event.asStartElement().name.localPart) {
                    CHANNEL -> parseChannel(channel, eventReader)
                }
            }
        }

        return channel.toFeed()
    }

    private fun parseChannel(channel: RssChannel, eventReader: XMLEventReader) {

        while (eventReader.hasNext()) {
            val event = eventReader.nextEvent()

            if (event.isStartElement) {
                when (event.asStartElement().name.localPart) {
                    TITLE -> channel.title = parseCharacterData(eventReader)
                    ITEM -> channel.items += RssItem().apply { parseItem(this, eventReader) }
                }
            }

            if (event.isEndElement && event.asEndElement().name.localPart == CHANNEL) {
                return
            }
        }
    }

    private fun parseItem(item: RssItem, eventReader: XMLEventReader) {

        while (eventReader.hasNext()) {
            val event = eventReader.nextEvent()

            if (event.isStartElement) {
                when (event.asStartElement().name.localPart) {
                    TITLE -> item.title = parseCharacterData(eventReader)
                    DESCRIPTION -> item.description = parseCharacterData(eventReader)
                    LINK -> item.link = parseCharacterData(eventReader)
                    GUID -> item.guid = parseCharacterData(eventReader)
                    PUBDATE -> item.pubDate = parseCharacterData(eventReader)
                    ENCLOSURE -> item.enclosures += RssEnclosure().apply { parseEnclosure(this, event.asStartElement()) }
                }
            }

            if (event.isEndElement && event.asEndElement().name.localPart == ITEM) {
                return
            }
        }
    }

    private fun parseEnclosure(enclosure: RssEnclosure, startElement: StartElement) {

        val attributes = startElement.attributes

        while (attributes.hasNext()) {
            val attr = attributes.next() as Attribute
            when (attr.name.localPart) {
                URL -> enclosure.url = attr.value
            }
        }
    }
}
