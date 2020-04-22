package com.munzenberger.feed

import org.junit.Assert.assertEquals
import org.junit.Test

class ParserTest {

    @Test
    fun `it can parse rss xml`() {

        val inStream = javaClass.getResourceAsStream("rss.xml")

        val document = parseXml(inStream)

        val node = document.firstChild

        assertEquals("rss", node.nodeName)
    }
}
