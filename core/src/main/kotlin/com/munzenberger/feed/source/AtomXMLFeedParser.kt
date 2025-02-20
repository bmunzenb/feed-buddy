package com.munzenberger.feed.source

import com.munzenberger.feed.Enclosure
import com.munzenberger.feed.Feed
import com.munzenberger.feed.Item
import org.apache.commons.text.StringEscapeUtils
import java.io.StringWriter
import javax.xml.stream.XMLEventReader
import javax.xml.stream.events.Attribute
import javax.xml.stream.events.StartElement

private class AtomFeed {
    var title = ""
    val entries = mutableListOf<AtomEntry>()
}

private class AtomEntry {
    var title = ""
    val links = mutableListOf<AtomLink>()
    var id = ""
    var updated = ""
    var summary = ""
    val contents = mutableListOf<AtomContent>()
    val categories = mutableListOf<AtomCategory>()
}

private class AtomLink {
    var rel = ""
    var type = ""
    var href = ""
}

private class AtomContent {
    var type = ""
    var value = ""
    val decodedValue: String
        get() =
            when (type) {
                "html" -> StringEscapeUtils.unescapeHtml4(value)
                else -> value
            }
}

private class AtomCategory {
    var term: String = ""
}

private fun AtomFeed.toFeed() =
    Feed(
        title = title,
        items = entries.map { it.toItem() },
    )

private fun AtomEntry.toItem() =
    Item(
        title = title,
        content = contents.firstOrNull()?.decodedValue ?: summary,
        link = links.firstOrNull { it.rel.isEmpty() || it.rel == "alternate" }?.href ?: "",
        guid = id,
        timestamp = updated,
        enclosures =
            links
                .filter { it.rel == "enclosure" }
                .map { Enclosure(it.href) },
        categories = categories.map { it.term },
    )

internal object AtomXMLFeedParser : XMLFeedParser {
    private const val TITLE = "title"
    private const val ENTRY = "entry"
    private const val LINK = "link"
    private const val REL = "rel"
    private const val TYPE = "type"
    private const val HREF = "href"
    private const val ID = "id"
    private const val UPDATED = "updated"
    private const val SUMMARY = "summary"
    private const val CONTENT = "content"
    private const val CATEGORY = "category"
    private const val CATEGORY_TERM = "term"

    override fun parse(eventReader: XMLEventReader): Feed {
        val feed = AtomFeed()

        while (eventReader.hasNext()) {
            val event = eventReader.nextEvent()

            if (event.isStartElement) {
                when (event.asStartElement().name.localPart) {
                    TITLE -> feed.title = parseCharacterData(eventReader)
                    ENTRY -> feed.entries += AtomEntry().apply { parseEntry(this, eventReader) }
                }
            }
        }

        return feed.toFeed()
    }

    @Suppress("CyclomaticComplexMethod", "NestedBlockDepth")
    private fun parseEntry(
        entry: AtomEntry,
        eventReader: XMLEventReader,
    ) {
        while (eventReader.hasNext()) {
            val event = eventReader.nextEvent()

            if (event.isStartElement) {
                when (event.asStartElement().name.localPart) {
                    TITLE -> entry.title = parseCharacterData(eventReader)
                    LINK -> entry.links += AtomLink().apply { parseLink(this, event.asStartElement()) }
                    ID -> entry.id = parseCharacterData(eventReader)
                    UPDATED -> entry.updated = parseCharacterData(eventReader)
                    SUMMARY -> entry.summary = parseCharacterData(eventReader)
                    CONTENT -> entry.contents += AtomContent().apply { parseContent(this, event.asStartElement(), eventReader) }
                    CATEGORY -> entry.categories += AtomCategory().apply { parseCategory(this, event.asStartElement()) }
                }
            }

            if (event.isEndElement && event.asEndElement().name.localPart == ENTRY) {
                return
            }
        }
    }

    private fun parseLink(
        link: AtomLink,
        startElement: StartElement,
    ) {
        val attributes = startElement.attributes

        while (attributes.hasNext()) {
            val attr = attributes.next() as Attribute
            when (attr.name.localPart) {
                REL -> link.rel = attr.value
                TYPE -> link.type = attr.value
                HREF -> link.href = attr.value
            }
        }
    }

    private fun parseContent(
        content: AtomContent,
        startElement: StartElement,
        eventReader: XMLEventReader,
    ) {
        val attributes = startElement.attributes

        while (attributes.hasNext()) {
            val attr = attributes.next() as Attribute
            when (attr.name.localPart) {
                TYPE -> content.type = attr.value
            }
        }

        val valueWriter = StringWriter()

        while (eventReader.hasNext()) {
            val event = eventReader.nextEvent()

            if (event.isEndElement && event.asEndElement().name.localPart == CONTENT) {
                content.value = valueWriter.toString()
                return
            }

            event.writeAsEncodedUnicode(valueWriter)
        }
    }

    private fun parseCategory(
        category: AtomCategory,
        startElement: StartElement,
    ) {
        val attributes = startElement.attributes

        while (attributes.hasNext()) {
            val attr = attributes.next() as Attribute
            when (attr.name.localPart) {
                CATEGORY_TERM -> category.term = attr.value
            }
        }
    }
}
