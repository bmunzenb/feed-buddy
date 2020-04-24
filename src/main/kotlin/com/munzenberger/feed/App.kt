package com.munzenberger.feed

import com.munzenberger.feed.source.XMLFeedSource
import java.io.File
import java.net.URL
import java.util.Properties

fun main(args: Array<String>) {

    val versionProperties = Properties().apply {
        val inStream = ClassLoader.getSystemResourceAsStream("version.properties")
        load(inStream)
    }

    val version = versionProperties["version"]

    println("Feed Buddy version $version (https://github.com/bmunzenb/feed-buddy)")

    System.setProperty("http.agent", "Feed-Buddy/$version (+https://github.com/bmunzenb/feed-buddy)")
    System.setProperty("sun.net.client.defaultConnectTimeout", "30000")
    System.setProperty("sun.net.client.defaultReadTimeout", "30000")

    if (args.isNotEmpty()) {
        // assume the first argument is a file containing feeds to parse
        parseFeeds(File(args[0]))
    }
}

private fun parseFeeds(file: File) {

    var results = 0 to 0

    file.readLines().forEach { source ->
        results = try {
            print("$source ... ")
            val feed = XMLFeedSource(URL(source)).read()
            println("SUCCESS: ${feed.title}, ${feed.items.size} item(s)")
            results.first+1 to results.second
        } catch (e: Throwable) {
            println("ERROR: [${e.javaClass.simpleName}] ${e.message}")
            results.first to results.second+1
        }
    }

    println("Processed ${results.first + results.second} feeds, ${results.first} success(es) and ${results.second} failure(s).")
}
