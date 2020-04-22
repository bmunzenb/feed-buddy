package com.munzenberger.feed

data class Feed(
        val channels: List<Channel>
)

data class Channel(
        val title: String,
        val items: List<Item>
)

data class Item(
        val title: String,
        val content: String,
        val link: String,
        val guid: String,
        val timestamp: String,
        val enclosures: List<Enclosure>
)

data class Enclosure(
        val link: String
)
