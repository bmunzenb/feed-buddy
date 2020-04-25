package com.munzenberger.feed.config

import com.munzenberger.feed.handler.DownloadEnclosures
import java.io.File

fun main(args: Array<String>) {

    val config = AppConfig(
            feeds = listOf(
                    FeedConfig(
                            url = "http://www.example.com/feed.xml",
                            handlers = listOf(
                                    ItemHandlerConfig(
                                            type = DownloadEnclosures::class.qualifiedName,
                                            properties = mapOf("targetDirectory" to "C:\\Downloads")
                                    )
                            )
                    )
            )
    )

    val jsonFile = File("D:\\config.json")

    JsonAppConfigAdapter.write(config, jsonFile)
    println("Wrote sample JSON config to $jsonFile.")
}
