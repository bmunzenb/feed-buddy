package com.munzenberger.feed.config

import com.munzenberger.feed.Item
import com.munzenberger.feed.handler.ItemHandler
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test

class ItemHandlerFactoryTest {

    class TestItemHandler : ItemHandler {
        var stringProperty: String = "default"
        var booleanProperty: Boolean = false
        var numberProperty: Int = 0
        override fun execute(item: Item) {}
    }

    @Test
    fun `it can construct a new item handler`() {

        val config = ItemHandlerConfig(
                type = TestItemHandler::class.java.name,
                properties = mapOf(
                        "stringProperty" to "testValue",
                        "booleanProperty" to true,
                        "numberProperty" to 42
                )
        )

        val handler = ItemHandlerFactory.newItemHandler(config)

        if (handler is TestItemHandler) {
            assertEquals("testValue", handler.stringProperty)
            assertEquals(true, handler.booleanProperty)
            assertEquals(42, handler.numberProperty)
        } else {
            fail("handler is not as instance of TestItemHandler")
        }
    }

    @Test
    fun `it saves newly created handlers with names in global registry`() {

        val config = ItemHandlerConfig(
                name = "global",
                type = TestItemHandler::class.java.name
        )

        val registry = mutableMapOf<String, ItemHandler>()

        val factory = ItemHandlerFactory(registry)

        val handler = factory.getInstance(config)

        assertTrue(registry.contains("global"))
        assertEquals(handler, registry["global"])
    }

    @Test
    fun `it retrieves handlers with refs from global registry`() {

        val config = ItemHandlerConfig(
                ref = "global"
        )

        val global = TestItemHandler()

        val registry = mutableMapOf<String, ItemHandler>("global" to global)

        val factory = ItemHandlerFactory(registry)

        val handler = factory.getInstance(config)

        assertEquals(global, handler)
    }
}
