package com.munzenberger.feed.source

import com.munzenberger.feed.Enclosure
import com.munzenberger.feed.Feed
import com.munzenberger.feed.Item
import org.w3c.dom.Node
import org.w3c.dom.NodeList
import javax.xml.xpath.XPathConstants
import javax.xml.xpath.XPathFactory

internal class RssXMLFeedParser(private val xPathFactory: XPathFactory) : XMLFeedParser {

    private val channelPath = xPathFactory.newXPath().compile("/rss/channel")
    private val titlePath = xPathFactory.newXPath().compile("title")
    private val itemPath = xPathFactory.newXPath().compile("item")
    private val descriptionPath = xPathFactory.newXPath().compile("description")
    private val linkPath = xPathFactory.newXPath().compile("link")
    private val guidPath = xPathFactory.newXPath().compile("guid")
    private val pubDatePath = xPathFactory.newXPath().compile("pubDate")
    private val enclosurePath = xPathFactory.newXPath().compile("enclosure")

    override fun parse(node: Node): Feed {

        val channelNode = channelPath.evaluate(node, XPathConstants.NODE) as Node

        return parseChannel(channelNode)
    }

    private fun parseChannel(node: Node): Feed {

        val title = titlePath.evaluate(node, XPathConstants.STRING) as String

        val itemList = itemPath.evaluate(node, XPathConstants.NODESET) as NodeList

        val items = parseItems(itemList)

        return Feed(title, items)
    }

    private fun parseItems(nodeList: NodeList) = nodeList.asList().map { node ->

        val title = titlePath.evaluate(node, XPathConstants.STRING) as String

        val content = descriptionPath.evaluate(node, XPathConstants.STRING) as String

        val link = linkPath.evaluate(node, XPathConstants.STRING) as String

        val guid = guidPath.evaluate(node, XPathConstants.STRING) as String

        val timestamp = pubDatePath.evaluate(node, XPathConstants.STRING) as String

        val enclosureList = enclosurePath.evaluate(node, XPathConstants.NODESET) as NodeList

        val enclosures = parseEnclosures(enclosureList)

        Item(
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
