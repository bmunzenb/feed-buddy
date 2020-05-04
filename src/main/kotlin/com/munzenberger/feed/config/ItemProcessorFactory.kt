package com.munzenberger.feed.config

interface ItemProcessorFactory<out T> {
    fun getInstance(config: ItemProcessorConfig): T
    fun reset() {}
}
