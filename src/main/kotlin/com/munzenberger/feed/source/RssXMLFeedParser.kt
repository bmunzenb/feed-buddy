package com.munzenberger.feed.source

import com.munzenberger.feed.Enclosure
import com.munzenberger.feed.Feed
import com.munzenberger.feed.Item
import org.w3c.dom.Node
import org.w3c.dom.NodeList
import javax.xml.xpath.XPathConstants
import javax.xml.xpath.XPathFactory

internal class RssXMLFeedParser(private val xPathFactory: XPathFactory) : XMLFeedParser {

    override fun parse(node: Node): Feed {

        val channelNode = xPathFactory.newXPath()
                .compile("/rss/channel")
                .evaluate(node, XPathConstants.NODE) as Node

        return parseChannel(channelNode)
    }

    private fun parseChannel(node: Node): Feed {

        val title = xPathFactory.newXPath()
                .compile("title")
                .evaluate(node, XPathConstants.STRING) as String

        val itemList = xPathFactory.newXPath()
                .compile("item")
                .evaluate(node, XPathConstants.NODESET) as NodeList

        val items = parseItems(title, itemList)

        return Feed(title, items)
    }

    private fun parseItems(feedTitle: String, nodeList: NodeList) = nodeList.asList().map { node ->

        val title = xPathFactory.newXPath()
                .compile("title")
                .evaluate(node, XPathConstants.STRING) as String

        val content = xPathFactory.newXPath()
                .compile("description")
                .evaluate(node, XPathConstants.STRING) as String

        val link = xPathFactory.newXPath()
                .compile("link")
                .evaluate(node, XPathConstants.STRING) as String

        val guid = xPathFactory.newXPath()
                .compile("guid")
                .evaluate(node, XPathConstants.STRING) as String

        val timestamp = xPathFactory.newXPath()
                .compile("pubDate")
                .evaluate(node, XPathConstants.STRING) as String

        val enclosureList = xPathFactory.newXPath()
                .compile("enclosure")
                .evaluate(node, XPathConstants.NODESET) as NodeList

        val enclosures = parseEnclosures(enclosureList)

        Item(
                feedTitle = feedTitle,
                title = title,
                content = content,
                link = link,
                guid = guid,
                timestamp = timestamp,
                enclosures = enclosures
        )
    }

    private fun parseEnclosures(nodeList: NodeList) = nodeList.asList().map { node ->

        val url = node.attributes
                .getNamedItem("url")
                .nodeValue

        Enclosure(url)
    }
}
