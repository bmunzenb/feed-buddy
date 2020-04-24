package com.munzenberger.feed.config

data class AppConfig(
        val period: Int = 360, // default 3 hours
        val handlers: List<ItemHandlerConfig> = emptyList(),
        val feeds: List<FeedConfig>
)

data class FeedConfig(
        val url: String,
        val period: Int? = null,
        val userAgent: String? = null,
        val handlers: List<ItemHandlerConfig> = emptyList()
)

data class ItemHandlerConfig(
        val name: String? = null,
        val ref: String? = null,
        val type: String? = null,
        val properties: Map<String,Any> = emptyMap()
)
