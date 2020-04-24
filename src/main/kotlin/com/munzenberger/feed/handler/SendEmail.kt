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
    lateinit var smtpHost: String
    lateinit var smtpPort: String

    var auth: String = ""
    var startTLSEnable: String = "false"
    var startTLSRequired: String = "false"
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
            setFrom("feedbuddy@noreply.com", item.feedTitle)
            subject = item.title
            setHtmlMsg(toHtmlMessage(item))
            mailSession = session
            buildMimeMessage()
        }

        if (!transport.isConnected) {
            print("Connecting to mail transport $smtpHost:$smtpPort ... ")
            transport.connect(smtpHost, smtpPort.toInt(), username, password)
            println("connection established.")
        }

        print("Sending email to $to ... ")
        val message = htmlEmail.mimeMessage
        val recipients = arrayOf<Address>(InternetAddress(to))
        transport.sendMessage(message, recipients)
        println("sent.")
    }

    private fun toHtmlMessage(item: Item): String {
        val mailItem = MailItem(item)
        val context = VelocityContext().apply { put("item", mailItem) }
        val writer = StringWriter()
        val reader = InputStreamReader(javaClass.getResourceAsStream("SendMail.vm"))
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
