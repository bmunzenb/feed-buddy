package com.munzenberger.feed.source

import com.munzenberger.feed.Enclosure
import com.munzenberger.feed.Feed
import com.munzenberger.feed.Item
import org.w3c.dom.Node
import org.w3c.dom.NodeList
import org.w3c.dom.ls.DOMImplementationLS
import javax.xml.xpath.XPathConstants
import javax.xml.xpath.XPathFactory

internal class AtomXMLFeedParser(private val xPathFactory: XPathFactory) : XMLFeedParser {

    private val feedTitlePath = xPathFactory.newXPath().compile("/feed/title")
    private val entryPath = xPathFactory.newXPath().compile("/feed/entry")
    private val itemTitlePath = xPathFactory.newXPath().compile("title")
    private val contentPath = xPathFactory.newXPath().compile("content")
    private val linkPath = xPathFactory.newXPath().compile("link[not(@rel) or @rel='alternate']")
    private val idPath = xPathFactory.newXPath().compile("id")
    private val timestampPath = xPathFactory.newXPath().compile("updated")

    // Feeds from YouTube use the following node for content description
    private val mediaGroupDescription = xPathFactory.newXPath().compile("group/description")

    override fun parse(node: Node): Feed {

        val title = feedTitlePath.evaluate(node, XPathConstants.STRING) as String

        val itemList = entryPath.evaluate(node, XPathConstants.NODESET) as NodeList

        val items = parseItems(title, itemList)

        return Feed(title, items)
    }

    private fun parseItems(feedTitle: String, nodeList: NodeList) = nodeList.asList().map { node ->

        val title = itemTitlePath.evaluate(node, XPathConstants.STRING) as String

        val contentNode = contentPath.evaluate(node, XPathConstants.NODE) as Node?
        val description = mediaGroupDescription.evaluate(node, XPathConstants.STRING) as String?

        val content = when {
            contentNode != null -> contentNode.innerXml
            description != null -> description
            else -> ""
        }

        val linkList = linkPath.evaluate(node, XPathConstants.NODESET) as NodeList

        val linkNode = linkList.asList().firstOrNull()

        val link = linkNode?.attributes?.getNamedItem("href")?.nodeValue ?: ""

        val guid = idPath.evaluate(node, XPathConstants.STRING) as String

        val timestamp = timestampPath.evaluate(node, XPathConstants.STRING) as String

        // TODO: parse enclosures
        val enclosures = emptyList<Enclosure>()

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
}

private val Node.innerXml: String
    get() {
        // https://stackoverflow.com/questions/3300839/get-a-nodes-inner-xml-as-string-in-java-dom
        val lsImpl = ownerDocument.implementation.getFeature("LS", "3.0") as DOMImplementationLS
        val serializer = lsImpl.createLSSerializer().apply {
            domConfig.setParameter("xml-declaration", false)
        }
        val nodes = childNodes
        val sb = StringBuilder()
        for (i in 0 until nodes.length) {
            val n = nodes.item(i)
            val s = serializer.writeToString(n)
            sb.append(s)
        }
        return sb.toString()
    }
