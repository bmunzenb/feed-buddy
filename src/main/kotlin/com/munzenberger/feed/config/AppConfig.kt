package com.munzenberger.feed.config

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement

@JacksonXmlRootElement(localName = "feeds")
data class AppConfig(
        val period: Int = 360, // default 3 hours
        @JacksonXmlProperty(localName = "handler")
        val handlers: List<ItemHandlerConfig> = emptyList(),
        @JacksonXmlProperty(localName = "feed")
        val feeds: List<FeedConfig>
)

data class FeedConfig(
        val url: String,
        val period: Int? = null,
        val userAgent: String? = null,
        @JacksonXmlProperty(localName = "handler")
        val handlers: List<ItemHandlerConfig> = emptyList()
)

data class ItemHandlerConfig(
        val name: String? = null,
        val ref: String? = null,
        val type: String? = null,
        val properties: Map<String,Any> = emptyMap()
)
