package com.munzenberger.feed.config

import java.io.File

interface AppConfigProvider {
    val name: String
    val config: AppConfig
}

class FileAppConfigProvider(private val file: File) : AppConfigProvider {

    override val name: String = file.path

    private val adapter = when {
        file.path.endsWith(".json") -> JsonAppConfigAdapter
        file.path.endsWith(".yaml") -> YamlAppConfigAdapter
        else -> XmlAppConfigAdapter
    }

    override val config: AppConfig
        get() = adapter.read(file)
}
