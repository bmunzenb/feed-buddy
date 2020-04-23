package com.munzenberger.feed.source

import com.munzenberger.feed.Feed

interface FeedSource {
    fun read(): Feed
}
