package com.munzenberger.feed.config

import com.munzenberger.feed.handler.SendEmail
import java.nio.file.Paths

fun main(args: Array<String>) {
    minimal()
}

private fun write(config: AppConfig, name: String) {

    val jsonFile = Paths.get("sample-config", "$name.json").toFile()
    JsonAppConfigAdapter.write(config, jsonFile)
    println("Wrote JSON config to $jsonFile.")

    val xmlFile = Paths.get("sample-config", "$name.xml").toFile()
    XmlAppConfigAdapter.write(config, xmlFile)
    println("Wrote XML config to $xmlFile.")

    val yamlFile = Paths.get("sample-config", "$name.yaml").toFile()
    YamlAppConfigAdapter.write(config, yamlFile)
    println("Wrote YAML config to $yamlFile.")
}

private fun minimal() {

    val config = AppConfig(
            feeds = listOf(
                    FeedConfig(
                            url = "http://www.example.com/feed.xml",
                            handlers = listOf(
                                    ItemProcessorConfig(
                                            type = SendEmail::class.java.name,
                                            properties = mapOf(
                                                    "to" to "example@mail.com",
                                                    "from" to "feedbuddy@example.com",
                                                    "smtpHost" to "mail.smtp.com",
                                                    "smtpPort" to 23,
                                                    "auth" to true,
                                                    "username" to "feedbuddy",
                                                    "password" to "fizzbuzz"
                                            )
                                    )
                            )
                    )
            )
    )

    write(config, "minimal")
}
