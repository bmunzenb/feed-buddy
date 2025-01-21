package com.munzenberger.feed.handler

import com.munzenberger.feed.Enclosure
import com.munzenberger.feed.FeedContext
import com.munzenberger.feed.Item
import com.munzenberger.feed.Logger
import org.apache.commons.mail.HtmlEmail
import org.apache.velocity.VelocityContext
import org.apache.velocity.app.Velocity
import java.io.InputStreamReader
import java.io.StringWriter
import java.net.URL
import java.text.DecimalFormat
import java.util.Date
import java.util.Properties
import javax.mail.Address
import javax.mail.Session
import javax.mail.Transport
import javax.mail.internet.InternetAddress

class SendEmail : ItemHandler {
    companion object {
        val templateURL = SendEmail::class.java.getResource("SendEmail.vm")!!
    }

    lateinit var to: String
    lateinit var from: String
    lateinit var smtpHost: String

    var smtpPort: Int = 0
    var auth: Boolean = false
    var startTLSEnable: Boolean = false
    var startTLSRequired: Boolean = false
    var username: String = ""
    var password: String = ""

    private val session: Session by lazy {

        val properties =
            Properties().apply {
                put("mail.transport.protocol", "smtp")
                put("mail.smtp.host", smtpHost)
                put("mail.smtp.port", smtpPort)
                put("mail.smtp.auth", auth)
                put("mail.smtp.user", username)
                put("mail.smtp.password", password)
                put("mail.smtp.starttls.enable", startTLSEnable)
                put("mail.smtp.starttls.required", startTLSRequired)
            }

        Session.getInstance(properties)
    }

    private val transport: Transport by lazy {
        session.transport
    }

    override fun execute(
        context: FeedContext,
        item: Item,
        logger: Logger,
    ) {
        val htmlEmail =
            HtmlEmail().apply {
                addTo(to)
                setFrom(from, context.feedTitle)
                item.timestampAsInstant?.let { sentDate = Date.from(it) }
                subject = item.title
                setHtmlMsg(item.toHtmlMessage(templateURL))
                mailSession = session
                buildMimeMessage()
            }

        if (!transport.isConnected) {
            logger.print("Connecting to mail transport $smtpHost:$smtpPort... ")
            transport.connect(smtpHost, smtpPort, username, password)
            logger.println("connected.")
        }

        logger.print("Sending email to $to... ")
        val message = htmlEmail.mimeMessage
        val recipients = arrayOf<Address>(InternetAddress(to))
        transport.sendMessage(message, recipients)
        logger.println("sent.")
    }
}

internal fun Item.toHtmlMessage(template: URL): String {
    val mailItem = MailItem(this)
    val context = VelocityContext().apply { put("item", mailItem) }
    val writer = StringWriter()
    val reader = InputStreamReader(template.openStream())
    Velocity.evaluate(context, writer, template.filename, reader)
    return writer.toString()
}

class MailItem(
    item: Item,
) {
    val description: String = item.content.encodeForEmail()
    val link: String = item.link
    val id: String = item.guid
    val enclosures: List<Enclosure> = item.enclosures
}

private fun String.encodeForEmail(): String {
    val sb = StringBuilder()
    val df = DecimalFormat("0000")

    forEach { c ->
        val i = c.code
        when {
            i >= 0x7F -> sb.append("&#${df.format(i)};")
            else -> sb.append(c)
        }
    }

    return sb.toString()
}
