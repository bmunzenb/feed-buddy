package com.munzenberger.feed

import java.util.Properties

fun main(args: Array<String>) {

    val version = Properties().apply {
        val inStream = ClassLoader.getSystemResourceAsStream("version.properties")
        load(inStream)
    }

    println("Feed Buddy version ${version["version"]} (https://github.com/bmunzenb/feed-buddy)")
}
