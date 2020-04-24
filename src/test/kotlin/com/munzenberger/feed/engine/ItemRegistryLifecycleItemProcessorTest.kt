package com.munzenberger.feed.engine

import com.munzenberger.feed.Item
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ItemRegistryLifecycleItemProcessorTest {

    @Test
    fun `it returns true to process the item when not in the registry`() {

        val mockItem = mockk<Item>()

        val mockRegistry = mockk<ItemRegistry>().apply {
            every { contains(mockItem) } returns false
        }

        val lifecycle = ItemRegistryLifecycleItemProcessor(mockRegistry, mockk())

        assertTrue(lifecycle.preExecute(mockItem))
    }

    @Test
    fun `it returns false to not process the item when in the registry`() {

        val mockItem = mockk<Item>()

        val mockRegistry = mockk<ItemRegistry>().apply {
            every { contains(mockItem) } returns true
        }

        val lifecycle = ItemRegistryLifecycleItemProcessor(mockRegistry, mockk())

        assertFalse(lifecycle.preExecute(mockItem))
    }

    @Test
    fun `it adds the item to the registry when it was processed`() {

        val mockItem = mockk<Item>()

        val mockRegistry = mockk<ItemRegistry>().apply {
            every { add(mockItem) } returns Unit
        }

        val lifecycle = ItemRegistryLifecycleItemProcessor(mockRegistry, mockk())
        lifecycle.postExecute(mockItem, true)

        verify { mockRegistry.add(mockItem) }
    }

    @Test
    fun `it does not add the item to the registry when it was not processed`() {

        val mockItem = mockk<Item>()

        val mockRegistry = mockk<ItemRegistry>()

        val lifecycle = ItemRegistryLifecycleItemProcessor(mockRegistry, mockk())
        lifecycle.postExecute(mockItem, false)

        verify(inverse = true) { mockRegistry.add(mockItem) }
    }
}
