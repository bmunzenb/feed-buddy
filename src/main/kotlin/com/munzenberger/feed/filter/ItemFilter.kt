package com.munzenberger.feed.filter

import com.munzenberger.feed.Item

interface ItemFilter {
    fun evaluate(item: Item): Boolean
}
