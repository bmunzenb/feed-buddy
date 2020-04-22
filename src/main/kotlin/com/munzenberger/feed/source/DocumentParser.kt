package com.munzenberger.feed.source

import com.munzenberger.feed.Feed
import org.w3c.dom.Document

internal interface DocumentParser {
    fun parse(document: Document): Feed
}
