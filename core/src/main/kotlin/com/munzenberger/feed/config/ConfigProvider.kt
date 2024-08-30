package com.munzenberger.feed.config

import java.io.File

interface ConfigProvider {
    val name: String
    val config: OperatorConfig
    val timestamp: Long
}

class FileConfigProvider(private val file: File) : ConfigProvider {

    override val name: String
        get() = file.absolutePath

    private val adapter: JacksonConfigAdapter
        get() = when {
            file.path.endsWith(".json", true) -> JsonConfigAdapter
            file.path.endsWith(".yaml", true) -> YamlConfigAdapter
            else -> XmlConfigAdapter
        }

    override val config: OperatorConfig
        get() = adapter.read(file)

    override val timestamp: Long
        get() = file.lastModified()
}
