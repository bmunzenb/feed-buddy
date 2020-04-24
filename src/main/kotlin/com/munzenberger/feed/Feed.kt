package com.munzenberger.feed

data class Feed(
        val title: String,
        val items: List<Item>
)

data class Item(
        val feedTitle: String,
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
