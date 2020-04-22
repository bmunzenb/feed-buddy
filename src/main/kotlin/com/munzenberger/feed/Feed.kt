package com.munzenberger.feed

data class Feed(
        val title: String,
        val items: List<Item>
)

// TODO: add author
data class Item(
        val title: String,
        val content: String,
        val link: String,
        val guid: String,
        val timestamp: String,
        val enclosures: List<Enclosure>
)

data class Enclosure(
        val url: String
)
