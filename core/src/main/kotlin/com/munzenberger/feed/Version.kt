package com.munzenberger.feed

object Version {
    val current: String = javaClass.`package`?.implementationVersion ?: "SNAPSHOT"
}
