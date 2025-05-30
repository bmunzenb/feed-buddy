package com.munzenberger.feed.handler

import com.munzenberger.feed.Item
import org.junit.Assert.assertNotNull
import org.junit.Test

class SendEmailTest {
    @Test
    fun `it should format an Item into an email message`() {
        val item =
            Item(
                title = "Test email title",
                content = "Test email content",
                link = "Test email link",
                guid = "Test email guid",
                timestamp = "2023-02-23T20:48:53+00:00",
                enclosures = emptyList(),
                categories = emptyList(),
            )

        val message = item.toHtmlMessage(SendEmail.templateURL)

        assertNotNull(message)
    }
}
