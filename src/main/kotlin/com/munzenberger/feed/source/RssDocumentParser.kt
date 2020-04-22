package com.munzenberger.feed.source

import com.munzenberger.feed.Enclosure
import com.munzenberger.feed.Feed
import com.munzenberger.feed.Item
import org.w3c.dom.Document
import org.w3c.dom.Node
import org.w3c.dom.NodeList
import javax.xml.xpath.XPathConstants
import javax.xml.xpath.XPathFactory

internal class RssDocumentParser : DocumentParser {

    private val xPathFactory = XPathFactory.newInstance()

    override fun parse(document: Document): Feed {

        val channelNode = xPathFactory.newXPath()
                .compile("/rss/channel")
                .evaluate(document, XPathConstants.NODE) as Node

        return parseChannel(channelNode)
    }

    private fun parseChannel(node: Node): Feed {

        val title = xPathFactory.newXPath()
                .compile("title")
                .evaluate(node, XPathConstants.STRING) as String

        val itemList = xPathFactory.newXPath()
                .compile("item")
                .evaluate(node, XPathConstants.NODESET) as NodeList

        val items = parseItems(itemList)

        return Feed(title, items)
    }

    private fun parseItems(nodeList: NodeList): List<Item> {

        val list = mutableListOf<Item>()

        for (i in 0 until nodeList.length) {

            val node = nodeList.item(i)

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

            val item = Item(
                    title = title,
                    content = content,
                    link = link,
                    guid = guid,
                    timestamp = timestamp,
                    enclosures = enclosures
            )

            list.add(item)
        }

        return list
    }

    private fun parseEnclosures(nodeList: NodeList): List<Enclosure> {

        val list = mutableListOf<Enclosure>()

        for (i in 0 until nodeList.length) {

            val node = nodeList.item(i)

            val url = node.attributes
                    .getNamedItem("url")
                    .nodeValue

            val enclosure = Enclosure(url)

            list.add(enclosure)
        }

        return list
    }
}
