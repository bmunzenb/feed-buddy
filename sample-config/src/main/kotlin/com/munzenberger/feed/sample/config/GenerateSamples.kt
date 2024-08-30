package com.munzenberger.feed.sample.config

import com.munzenberger.feed.config.FeedConfig
import com.munzenberger.feed.config.ItemProcessorConfig
import com.munzenberger.feed.config.JsonConfigAdapter
import com.munzenberger.feed.config.OperatorConfig
import com.munzenberger.feed.config.XmlConfigAdapter
import com.munzenberger.feed.config.YamlConfigAdapter
import com.munzenberger.feed.handler.SendEmail
import java.nio.file.Paths

fun main() {
    minimal()
}

private fun write(config: OperatorConfig, name: String) {
    val jsonFile = Paths.get(".", "$name.json").toFile()
    print("Writing $name JSON config to $jsonFile ... ")
    JsonConfigAdapter.write(config, jsonFile)
    println("done.")

    val xmlFile = Paths.get(".", "$name.xml").toFile()
    print("Writing $name XML config to $jsonFile ... ")
    XmlConfigAdapter.write(config, xmlFile)
    println("done.")

    val yamlFile = Paths.get(".", "$name.yaml").toFile()
    print("Writing $name YAML config to $jsonFile ... ")
    YamlConfigAdapter.write(config, yamlFile)
    println("done.")
}

private fun minimal() {
    val config = OperatorConfig(
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
