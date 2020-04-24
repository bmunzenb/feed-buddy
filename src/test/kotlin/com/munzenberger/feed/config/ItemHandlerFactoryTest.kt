package com.munzenberger.feed.config

import com.munzenberger.feed.Item
import com.munzenberger.feed.handler.ItemHandler
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test
import java.lang.IllegalArgumentException

class ItemHandlerFactoryTest {

    class TestItemHandler : ItemHandler {
        var stringProperty: String = "default"
        var booleanProperty: Boolean = false
        var integerProperty: Int = 0
        override fun execute(item: Item) {}
    }

    @Test(expected = ClassNotFoundException::class)
    fun `it throws an exception when the type does not exist`() {

        val config = ItemHandlerConfig(
                type = "does.not.exist"
        )

        ItemHandlerFactory.newItemHandler(config)
    }

    @Test(expected = ClassCastException::class)
    fun `it throws an exception when the type is not an item handler`() {

        val config = ItemHandlerConfig(
                type = "java.lang.Object"
        )

        ItemHandlerFactory.newItemHandler(config)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `it throws an exception when a property does not exist`() {

        val config = ItemHandlerConfig(
                type = TestItemHandler::class.java.name,
                properties = mapOf("invalidProperty" to "testValue")
        )

        ItemHandlerFactory.newItemHandler(config)
    }

    @Test
    fun `it can construct a new item handler`() {

        val config = ItemHandlerConfig(
                type = TestItemHandler::class.java.name,
                properties = mapOf(
                        "stringProperty" to "testValue",
                        "booleanProperty" to true,
                        "integerProperty" to 42
                )
        )

        val handler = ItemHandlerFactory.newItemHandler(config)

        if (handler is TestItemHandler) {
            assertEquals("testValue", handler.stringProperty)
            assertEquals(true, handler.booleanProperty)
            assertEquals(42, handler.integerProperty)
        } else {
            fail("handler is not an instance of TestItemHandler")
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
