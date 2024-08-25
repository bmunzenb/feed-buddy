package com.munzenberger.feed.config

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement

@JacksonXmlRootElement(localName = "feeds")
data class AppConfig(
        @JacksonXmlProperty(isAttribute = true)
        val period: Int = 360, // default 3 hours
        @JacksonXmlProperty(localName = "handler")
        val handlers: List<ItemProcessorConfig> = emptyList(),
        @JacksonXmlProperty(localName = "filter")
        val filters: List<ItemProcessorConfig> = emptyList(),
        @JacksonXmlProperty(localName = "feed")
        val feeds: List<FeedConfig>
)

data class FeedConfig(
        @JacksonXmlProperty(isAttribute = true)
        val url: String,
        @JacksonXmlProperty(isAttribute = true)
        val period: Int? = null,
        @JacksonXmlProperty(isAttribute = true)
        val userAgent: String? = null,
        @JacksonXmlProperty(localName = "handler")
        val handlers: List<ItemProcessorConfig> = emptyList(),
        @JacksonXmlProperty(localName = "filter")
        val filters: List<ItemProcessorConfig> = emptyList()
)

data class ItemProcessorConfig(
        @JacksonXmlProperty(isAttribute = true)
        val name: String? = null,
        @JacksonXmlProperty(isAttribute = true)
        val ref: String? = null,
        @JacksonXmlProperty(isAttribute = true)
        val type: String? = null,
        val properties: Map<String,Any> = emptyMap()
)
