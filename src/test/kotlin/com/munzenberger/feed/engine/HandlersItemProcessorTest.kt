package com.munzenberger.feed.engine

import com.munzenberger.feed.Item
import com.munzenberger.feed.handler.ItemHandler
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.lang.Exception

class HandlersItemProcessorTest {

    @Test
    fun `it returns true when all handlers succeed`() {

        val mockItem = mockk<Item>()

        val mockHandler = mockk<ItemHandler>().apply {
            every { execute(mockItem) } returns Unit
        }

        val processor = HandlersItemProcessor(listOf(mockHandler, mockHandler, mockHandler))

        assertTrue(processor.execute(mockItem))

        verify(exactly = 3) { mockHandler.execute(mockItem) }
    }

    @Test
    fun `it returns false when at least one handler fails`() {

        val mockItem = mockk<Item>()

        val mockHandler1 = mockk<ItemHandler>().apply {
            every { execute(mockItem) } returns Unit
        }

        val mockHandler2 = mockk<ItemHandler>().apply {
            every { execute(mockItem) } throws Exception()
        }

        val mockHandler3 = mockk<ItemHandler>().apply {
            every { execute(mockItem) } returns Unit
        }

        val processor = HandlersItemProcessor(listOf(mockHandler1, mockHandler2, mockHandler3))

        assertFalse(processor.execute(mockItem))

        verify { mockHandler1.execute(mockItem) }
        verify { mockHandler2.execute(mockItem) }
        verify(inverse = true) { mockHandler3.execute(mockItem) }
    }
}
