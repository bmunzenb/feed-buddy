package com.munzenberger.feed.config

import org.junit.Assert
import org.junit.Test

class XmlOperatorConfigAdapterTest {
    @Test
    fun `it can parse a config file`() {
        val source = javaClass.getResourceAsStream("config.xml")
        val config = XmlConfigAdapter.read(source)

        val expected =
            OperatorConfig(
                period = 42,
                handlers =
                    listOf(
                        ItemProcessorConfig(
                            name = "global handler",
                            type = "com.test.Class",
                            properties = mapOf("foo" to "bar", "fizz" to "32"),
                        ),
                    ),
                feeds =
                    listOf(
                        FeedConfig(
                            url = "http://www.example.com/feed.xml",
                            userAgent = "test user agent",
                            period = 86,
                            handlers =
                                listOf(
                                    ItemProcessorConfig(
                                        type = "com.test.Handler",
                                        properties = mapOf("bar" to "foo", "boolean" to "true"),
                                    ),
                                    ItemProcessorConfig(
                                        ref = "global handler",
                                    ),
                                ),
                        ),
                    ),
            )

        Assert.assertEquals(expected, config)
    }
}
