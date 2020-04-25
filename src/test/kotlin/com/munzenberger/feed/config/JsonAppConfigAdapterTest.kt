package com.munzenberger.feed.config

import org.junit.Assert.assertEquals
import org.junit.Test

class JsonAppConfigAdapterTest {

    @Test
    fun `it can parse a config file`() {

        val source = javaClass.getResourceAsStream("config.json")
        val config = JsonAppConfigAdapter.read(source)

        val expected = AppConfig(
                period = 42,
                handlers = listOf(
                        ItemHandlerConfig(
                                name = "global handler",
                                type = "com.test.Class",
                                properties = mapOf("foo" to "bar", "fizz" to 32.0)
                        )
                ),
                feeds = listOf(
                        FeedConfig(
                                url = "http://www.example.com/feed.xml",
                                userAgent = "test user agent",
                                period = 86,
                                handlers = listOf(
                                        ItemHandlerConfig(
                                                type = "com.test.Handler",
                                                properties = mapOf("bar" to "foo", "boolean" to true)
                                        ),
                                        ItemHandlerConfig(
                                                ref = "global handler"
                                        )
                                )
                        )
                )
        )

        assertEquals(expected, config)
    }
}
