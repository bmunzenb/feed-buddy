package com.munzenberger.feed.config

import com.munzenberger.feed.handler.DownloadEnclosures
import com.munzenberger.feed.handler.SendEmail
import java.io.File

fun main(args: Array<String>) {

    val handler1 = ItemHandlerConfig(
            name = "Download Enclosures",
            type = DownloadEnclosures::class.qualifiedName,
            properties = mapOf("targetDirectory" to "C:\\Downloads")
    )

    val handler2 = ItemHandlerConfig(
            name = "Send Email",
            type = SendEmail::class.qualifiedName,
            properties = mapOf(
                    "to" to "recipient@email.com",
                    "from" to "sender@email.com",
                    "smtpHost" to "smtp.mail.com",
                    "smtpPort" to 25
            )
    )

    val feedConfig = FeedConfig(
            url = "http://www.example.com/feed.xml",
            handlers = listOf(handler1, handler2)
    )

    val config = AppConfig(
            period = 480,
            handlers = listOf(handler1, handler2),
            feeds = listOf(feedConfig, feedConfig)
    )

    val jsonFile = File("D:\\config.json")
    JsonAppConfigAdapter.write(config, jsonFile)
    println("Wrote sample JSON config to $jsonFile.")

    val xmlFile = File("D:\\config.xml")
    XmlAppConfigAdapter.write(config, xmlFile)
    println("Wrote sample XML config to $jsonFile.")

    val yamlFile = File("D:\\config.yaml")
    YamlAppConfigAdapter.write(config, yamlFile)
    println("Wrote sample YAML config to $jsonFile.")
}
