package com.munzenberger.feed.config

import java.nio.file.Files
import java.nio.file.Path

interface ConfigProvider {
    val name: String
    val config: OperatorConfig
    val timestamp: Long
}

class FileConfigProvider(
    private val file: Path,
) : ConfigProvider {
    override val name: String
        get() = file.toAbsolutePath().toString()

    private val adapter: JacksonConfigAdapter
        get() =
            when {
                file.toString().endsWith(".json", true) -> JsonConfigAdapter
                file.toString().endsWith(".yaml", true) -> YamlConfigAdapter
                file.toString().endsWith(".yml", true) -> YamlConfigAdapter
                else -> XmlConfigAdapter
            }

    override val config: OperatorConfig
        get() = adapter.read(file)

    override val timestamp: Long
        get() = Files.getLastModifiedTime(file).toMillis()
}
