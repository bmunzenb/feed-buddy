package com.munzenberger.feed.source

import com.munzenberger.feed.Feed
import javax.xml.stream.XMLEventReader

interface XMLFeedParser {
    fun parse(eventReader: XMLEventReader): Feed

    fun parseCharacterData(eventReader: XMLEventReader) =
        buildString {
            var event = eventReader.nextEvent()
            while (event.isCharacters) {
                val data = event.asCharacters().data
                append(data)
                event = eventReader.nextEvent()
            }
        }
}
