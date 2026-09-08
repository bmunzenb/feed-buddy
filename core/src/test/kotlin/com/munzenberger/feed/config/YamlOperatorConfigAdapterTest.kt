package com.munzenberger.feed.config

import org.junit.Assert.assertEquals
import org.junit.Test

class YamlOperatorConfigAdapterTest {
    @Test
    fun `it can parse a config file`() {
        val source = javaClass.getResourceAsStream("config.yaml")
        val config = YamlConfigAdapter.read(source)

        val expected =
            OperatorConfig(
                period = 42,
                delay = 7,
                handlers =
                    listOf(
                        ItemProcessorConfig(
                            name = "global handler",
                            type = "com.test.Class",
                            properties = mapOf("foo" to "bar", "fizz" to 32),
                        ),
                    ),
                feeds =
                    listOf(
                        FeedConfig(
                            url = "http://www.example.com/feed.xml",
                            userAgent = "test user agent",
                            period = 86,
                            delay = 15,
                            handlers =
                                listOf(
                                    ItemProcessorConfig(
                                        type = "com.test.Handler",
                                        properties = mapOf("bar" to "foo", "boolean" to true),
                                    ),
                                    ItemProcessorConfig(
                                        ref = "global handler",
                                    ),
                                ),
                        ),
                    ),
            )

        assertEquals(expected, config)
    }
}
