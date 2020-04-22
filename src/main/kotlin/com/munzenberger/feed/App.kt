package com.munzenberger.feed

import com.munzenberger.feed.source.URLFeedSource
import java.io.File
import java.net.URL
import java.util.Properties

fun main(args: Array<String>) {

    val version = Properties().apply {
        val inStream = ClassLoader.getSystemResourceAsStream("version.properties")
        load(inStream)
    }

    println("Feed Buddy version ${version["version"]} (https://github.com/bmunzenb/feed-buddy)")

    if (args.size > 0) {
        // assume the first argument is a file containing feeds to parse
        parseFeeds(File(args[0]))
    }
}

private fun parseFeeds(file: File) {

    var results = 0 to 0

    file.readLines().forEach { source ->
        try {
            print("$source ... ")
            val feed = URLFeedSource(source, URL(source)).read()
            println("SUCCESS: ${feed.title}, ${feed.items.size} item(s)")
            results = results.first+1 to results.second
        } catch (e: Throwable) {
            println("ERROR: [${e.javaClass.simpleName}] ${e.message}")
            results = results.first to results.second+1
        }
    }

    println("Processed ${results.first + results.second} feeds, ${results.first} success(es) and ${results.second} failure(s).")
}
