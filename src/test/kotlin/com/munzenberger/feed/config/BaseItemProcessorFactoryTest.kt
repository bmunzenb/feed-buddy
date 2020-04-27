package com.munzenberger.feed.config

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class BaseItemProcessorFactoryTest {

    class TestItemProcessor {
        var stringProperty: String = "default"
        var booleanProperty: Boolean = false
        var integerProperty: Int = 0
    }

    @Test(expected = ClassNotFoundException::class)
    fun `it throws an exception when the type does not exist`() {

        val config = ItemProcessorConfig(
                type = "does.not.exist"
        )

        BaseItemProcessorFactory.newItemProcessor<TestItemProcessor>(config)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `it throws an exception when a property does not exist`() {

        val config = ItemProcessorConfig(
                type = TestItemProcessor::class.java.name,
                properties = mapOf("invalidProperty" to "testValue")
        )

        BaseItemProcessorFactory.newItemProcessor<TestItemProcessor>(config)
    }

    @Test
    fun `it can construct a new item handler`() {

        val config = ItemProcessorConfig(
                type = TestItemProcessor::class.java.name,
                properties = mapOf(
                        "stringProperty" to "testValue",
                        "booleanProperty" to true,
                        "integerProperty" to 42
                )
        )

        val handler = BaseItemProcessorFactory.newItemProcessor<TestItemProcessor>(config)

        assertEquals("testValue", handler.stringProperty)
        assertEquals(true, handler.booleanProperty)
        assertEquals(42, handler.integerProperty)
    }

    @Test
    fun `it saves newly created handlers with names in global registry`() {

        val config = ItemProcessorConfig(
                name = "global",
                type = TestItemProcessor::class.java.name
        )

        val registry = mutableMapOf<String, TestItemProcessor>()

        val factory = BaseItemProcessorFactory(registry)

        val handler = factory.getInstance(config)

        assertTrue(registry.contains("global"))
        assertEquals(handler, registry["global"])
    }

    @Test
    fun `it retrieves processes with refs from global registry`() {

        val config = ItemProcessorConfig(
                ref = "global"
        )

        val global = TestItemProcessor()

        val registry = mutableMapOf("global" to global)

        val factory = BaseItemProcessorFactory(registry)

        val process = factory.getInstance(config)

        assertEquals(global, process)
    }
}
