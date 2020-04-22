package com.munzenberger.feed

import org.w3c.dom.Document
import org.w3c.dom.Node
import java.io.InputStream
import java.lang.IllegalStateException
import java.lang.UnsupportedOperationException
import javax.xml.parsers.DocumentBuilderFactory

fun parseXml(inStream: InputStream): Document {

    val dbFactory = DocumentBuilderFactory.newInstance()
    val dBuilder = dbFactory.newDocumentBuilder()
    return dBuilder.parse(inStream)
}

fun Document.toFeed(): Feed {

    val root = firstChild

    val parser = when (val type = root.nodeName) {
        "rss" -> RssParser()
        else -> throw IllegalStateException("Unrecognized feed type: $type")
    }

    return parser.toFeed(root)
}

interface Parser {
    fun toFeed(node: Node): Feed
}

class RssParser : Parser {
    override fun toFeed(node: Node): Feed {
        throw UnsupportedOperationException()
    }
}
