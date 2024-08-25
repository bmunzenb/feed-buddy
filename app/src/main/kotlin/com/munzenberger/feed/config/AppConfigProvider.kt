package com.munzenberger.feed.config

import java.io.File

interface AppConfigProvider {
    val name: String
    val config: AppConfig
    val timestamp: Long
}

class FileAppConfigProvider(private val file: File) : AppConfigProvider {

    override val name: String
        get() = file.absolutePath

    private val adapter: JacksonAppConfigAdapter
        get() = when {
            file.path.endsWith(".json", true) -> JsonAppConfigAdapter
            file.path.endsWith(".yaml", true) -> YamlAppConfigAdapter
            else -> XmlAppConfigAdapter
        }

    override val config: AppConfig
        get() = adapter.read(file)

    override val timestamp: Long
        get() = file.lastModified()
}
