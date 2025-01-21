package com.munzenberger.feed.source

import com.munzenberger.feed.Feed

interface FeedSource {
    val name: String

    fun read(): Feed
}
