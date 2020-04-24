package com.munzenberger.feed.engine

import com.munzenberger.feed.Item
import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.verify
import org.junit.Test

class LifecycleItemProcessorTest {

    @Test
    fun `it does not process items when preExecute returns false`() {

        val mockItem = mockk<Item>()

        val mockProcessor = mockk<ItemProcessor>(relaxed = true)

        val lifecycle = object : LifecycleItemProcessor(mockProcessor) {
            override fun preExecute(item: Item) = false
        }

        val spy = spyk(lifecycle)
        spy.execute(mockItem)

        verify(inverse = true) { mockProcessor.execute(mockItem) }
        verify { spy.postExecute(mockItem, false) }
    }

    @Test
    fun `it processes items when preExecute returns true and passes the result to postExecute`() {

        val mockItem = mockk<Item>()

        val mockProcessor = mockk<ItemProcessor>().apply {
            every { execute(mockItem) } returns true
        }

        val lifecycle = object : LifecycleItemProcessor(mockProcessor) {
            override fun preExecute(item: Item) = true
        }

        val spy = spyk(lifecycle)
        spy.execute(mockItem)

        verify { mockProcessor.execute(mockItem) }
        verify { spy.postExecute(mockItem, true) }
    }
}
