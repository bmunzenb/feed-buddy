package com.munzenberger.feed.engine

import com.munzenberger.feed.config.ItemProcessorConfig
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class DefaultItemProcessorFactoryTest {
    class TestItemProcessor : ItemProcessor {
        var stringProperty: String = "default"
        var booleanProperty: Boolean = false
        var integerProperty: Int = 0
    }

    @Test(expected = ClassNotFoundException::class)
    fun `it throws an exception when the type does not exist`() {
        val config =
            ItemProcessorConfig(
                type = "does.not.exist",
            )

        DefaultItemProcessorFactory.newItemProcessor<TestItemProcessor>(config)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `it throws an exception when a property does not exist`() {
        val config =
            ItemProcessorConfig(
                type = TestItemProcessor::class.java.name,
                properties = mapOf("invalidProperty" to "testValue"),
            )

        DefaultItemProcessorFactory.newItemProcessor<TestItemProcessor>(config)
    }

    @Test
    fun `it can construct a new item handler`() {
        val config =
            ItemProcessorConfig(
                type = TestItemProcessor::class.java.name,
                properties =
                    mapOf(
                        "stringProperty" to "testValue",
                        "booleanProperty" to true,
                        "integerProperty" to 42,
                    ),
            )

        val handler = DefaultItemProcessorFactory.newItemProcessor<TestItemProcessor>(config)

        assertEquals("testValue", handler.stringProperty)
        assertEquals(true, handler.booleanProperty)
        assertEquals(42, handler.integerProperty)
    }

    @Test
    fun `it can construct a new item handler with coerced types`() {
        val config =
            ItemProcessorConfig(
                type = TestItemProcessor::class.java.name,
                properties =
                    mapOf(
                        "stringProperty" to "testValue",
                        "booleanProperty" to "true",
                        "integerProperty" to "42",
                    ),
            )

        val handler = DefaultItemProcessorFactory.newItemProcessor<TestItemProcessor>(config)

        assertEquals("testValue", handler.stringProperty)
        assertEquals(true, handler.booleanProperty)
        assertEquals(42, handler.integerProperty)
    }

    @Test
    fun `it saves newly created handlers with names in global registry`() {
        val config =
            ItemProcessorConfig(
                name = "global",
                type = TestItemProcessor::class.java.name,
            )

        val registry = mutableMapOf<String, TestItemProcessor>()

        val factory = DefaultItemProcessorFactory(registry)

        val handler = factory.getInstance(config)

        assertTrue(registry.contains("global"))
        assertEquals(handler, registry["global"])
    }

    @Test
    fun `it retrieves processor with refs from global registry`() {
        val config =
            ItemProcessorConfig(
                ref = "global",
            )

        val global = TestItemProcessor()

        val registry = mutableMapOf("global" to global)

        val factory = DefaultItemProcessorFactory(registry)

        val processor = factory.getInstance(config)

        assertEquals(global, processor)
    }
}
