package com.munzenberger.feed.handler

import com.munzenberger.feed.Enclosure
import com.munzenberger.feed.Item
import org.apache.commons.mail.HtmlEmail
import org.apache.velocity.VelocityContext
import org.apache.velocity.app.Velocity
import java.io.InputStreamReader
import java.io.StringWriter
import java.text.DecimalFormat
import java.util.Properties
import javax.mail.Address
import javax.mail.Session
import javax.mail.Transport
import javax.mail.internet.InternetAddress

class SendEmail : ItemHandler {

    lateinit var to: String
    lateinit var from: String
    lateinit var smtpHost: String

    // because JSON doesn't support integers...
    private var _smtpPort: Int = 0
    var smtpPort: Double
        get() = _smtpPort.toDouble()
        set(value) { _smtpPort = value.toInt() }

    var auth: Boolean = false
    var startTLSEnable: Boolean = false
    var startTLSRequired: Boolean = false
    var username: String = ""
    var password: String = ""

    private val session: Session by lazy {

        val properties = Properties().apply {
            put("mail.transport.protocol", "smtp")
            put("mail.smtp.host", smtpHost)
            put("mail.smtp.port", smtpPort)
            put("mail.smtp.auth", auth)
            put("mail.smtp.user", username)
            put("mail.smtp.password", password)
            put("mail.smtp.starttls.enable", startTLSEnable)
            put("mail.smtp.starttls.required", startTLSRequired)
        }

        Session.getDefaultInstance(properties)
    }

    private val transport: Transport by lazy {
        session.transport
    }

    override fun execute(item: Item) {

        val htmlEmail = HtmlEmail().apply {
            addTo(to)
            setFrom(from, item.feedTitle)
            subject = item.title
            setHtmlMsg(toHtmlMessage(item))
            mailSession = session
            buildMimeMessage()
        }

        if (!transport.isConnected) {
            print("Connecting to mail transport $smtpHost:$_smtpPort... ")
            transport.connect(smtpHost, _smtpPort, username, password)
            println("connected.")
        }

        print("Sending email for '${item.guid}' to $to... ")
        val message = htmlEmail.mimeMessage
        val recipients = arrayOf<Address>(InternetAddress(to))
        transport.sendMessage(message, recipients)
        println("sent.")
    }

    private fun toHtmlMessage(item: Item): String {
        val mailItem = MailItem(item)
        val context = VelocityContext().apply { put("item", mailItem) }
        val writer = StringWriter()
        val template = javaClass.getResourceAsStream("SendEmail.vm")
        val reader = InputStreamReader(template)
        Velocity.evaluate(context, writer, "", reader)
        return writer.toString()
    }
}

class MailItem(item: Item) {
    val description: String = item.content.encodeForEmail()
    val link: String = item.link
    val id: String = item.guid
    val enclosures: List<Enclosure> = item.enclosures
}

private fun String.encodeForEmail(): String {
    val sb = StringBuilder()
    val df = DecimalFormat("0000")

    forEach { c ->
        val i = c.toInt()
        when {
            i >= 0x7F -> sb.append("&#${df.format(i)};")
            else -> sb.append(c)
        }
    }

    return sb.toString()
}
