package com.munzenberger.feed.engine

import com.munzenberger.feed.config.ItemProcessorConfig

interface ItemProcessor

interface ItemProcessorFactory<out T : ItemProcessor> {
    fun getInstance(config: ItemProcessorConfig): T
    fun reset() {}
}
