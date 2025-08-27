package com.munzenberger.feed.handler

import com.munzenberger.feed.FeedContext
import com.munzenberger.feed.Item
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Test

class SendEmailTest {
    @Test
    fun `it should format an Item into an email message`() {
        val context =
            mockk<FeedContext> {
                every { sourceName } returns "test source"
            }

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

        val message = item.toHtmlMessage(SendEmail.templateURL, context)

        val expected =
            """
            
            <html>
            	<head></head>
            	<body>
            		<div>
            				Test email content
            		</div>
            		<hr>
            		<div>
            				<p>Article: <a href="Test email link">Test email link</a></p>
            		</div>
            	</body>
            </html>
            <!-- Feed source: test source -->
            <!-- Item ID: Test email guid -->

            """.trimIndent()

        assertEquals(expected, message)
    }
}
