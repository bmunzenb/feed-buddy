package com.munzenberger.feed.source

import com.munzenberger.feed.Enclosure
import com.munzenberger.feed.Feed
import com.munzenberger.feed.Item
import org.w3c.dom.Node
import org.w3c.dom.NodeList
import org.w3c.dom.ls.DOMImplementationLS
import javax.xml.xpath.XPathConstants
import javax.xml.xpath.XPathFactory

internal class AtomDocumentParser : DocumentParser {

    private val xPathFactory = XPathFactory.newInstance()

    override fun parse(node: Node): Feed {

        val title = xPathFactory.newXPath()
                .compile("/feed/title")
                .evaluate(node, XPathConstants.STRING) as String

        val itemList = xPathFactory.newXPath()
                .compile("/feed/entry")
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

            val contentNode = xPathFactory.newXPath()
                    .compile("content[not(@type) or @type='xhtml']")
                    .evaluate(node, XPathConstants.NODE) as Node?

            val content = contentNode?.innerXml ?: ""

            val linkList = xPathFactory.newXPath()
                    .compile("link[not(@rel) or @rel='alternate']")
                    .evaluate(node, XPathConstants.NODESET) as NodeList

            val linkNode = when {
                linkList.length > 0 -> linkList.item(0)
                else -> null
            }

            val link = linkNode?.attributes?.getNamedItem("href")?.nodeValue ?: ""

            val guid = xPathFactory.newXPath()
                    .compile("id")
                    .evaluate(node, XPathConstants.STRING) as String

            val timestamp = xPathFactory.newXPath()
                    .compile("updated")
                    .evaluate(node, XPathConstants.STRING) as String

            // TODO: parse enclosures
            val enclosures = emptyList<Enclosure>()

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
