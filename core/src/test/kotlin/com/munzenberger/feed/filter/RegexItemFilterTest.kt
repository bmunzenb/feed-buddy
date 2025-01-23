package com.munzenberger.feed.filter

import com.munzenberger.feed.FeedContext
import com.munzenberger.feed.Item
import com.munzenberger.feed.Logger
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class RegexItemFilterTest {
    private val context = mockk<FeedContext>()
    private val logger = mockk<Logger>()

    @Test
    fun `it filters items based on title`() {
        val item = mockk<Item>()
        every { item.title } returns "Foo"
        every { item.content } returns "Contents"
        every { item.categories } returns emptyList()

        val filterInclude =
            RegexItemFilter().apply {
                title = ".*Foo.*"
            }

        assertTrue(filterInclude.evaluate(context, item, logger))

        val filterExclude =
            RegexItemFilter().apply {
                title = ".*Bar.*"
            }

        assertFalse(filterExclude.evaluate(context, item, logger))
    }

    @Test
    fun `it filters items based on content`() {
        val item = mockk<Item>()
        every { item.title } returns "Title"
        every { item.content } returns "The quick brown fox"
        every { item.categories } returns emptyList()

        val filterInclude =
            RegexItemFilter().apply {
                content = ".*quick.*"
            }

        assertTrue(filterInclude.evaluate(context, item, logger))

        val filterExclude =
            RegexItemFilter().apply {
                content = ".*slow.*"
            }

        assertFalse(filterExclude.evaluate(context, item, logger))
    }

    @Test
    fun `it filters items based on categories`() {
        val item = mockk<Item>()
        every { item.title } returns "Title"
        every { item.content } returns "Content"
        every { item.categories } returns listOf("Category A", "Category B")

        val filterInclude =
            RegexItemFilter().apply {
                category = ".*Category A.*"
            }

        assertTrue(filterInclude.evaluate(context, item, logger))

        val filterExclude =
            RegexItemFilter().apply {
                content = ".*Category C.*"
            }

        assertFalse(filterExclude.evaluate(context, item, logger))
    }

    @Test
    fun `it filters items with no categories`() {
        val item = mockk<Item>()
        every { item.title } returns "Title"
        every { item.content } returns "Content"
        every { item.categories } returns emptyList()

        val filter =
            RegexItemFilter().apply {
                category = ".*foo.*"
            }

        assertFalse(filter.evaluate(context, item, logger))
    }
}
