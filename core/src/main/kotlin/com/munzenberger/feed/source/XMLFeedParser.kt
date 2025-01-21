package com.munzenberger.feed.source

import com.munzenberger.feed.Feed
import javax.xml.stream.XMLEventReader

interface XMLFeedParser {
    fun parse(eventReader: XMLEventReader): Feed

    fun parseCharacterData(eventReader: XMLEventReader): String {
        var value = ""

        val event = eventReader.nextEvent()
        if (event.isCharacters) {
            value = event.asCharacters().data
        }

        return value
    }
}
