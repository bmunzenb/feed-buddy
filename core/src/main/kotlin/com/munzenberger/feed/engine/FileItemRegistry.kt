package com.munzenberger.feed.engine

import com.munzenberger.feed.Item
import com.munzenberger.feed.config.FeedConfig
import com.munzenberger.feed.filterForPath
import java.net.URI
import java.net.URL
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardOpenOption

class FileItemRegistryFactory(private val basePath: Path) : ItemRegistryFactory {
    override fun getInstance(feedConfig: FeedConfig): ItemRegistry {
        val key =
            feedConfig.registryKey?.filterForPath()
                ?: URI(feedConfig.url).toURL().registryFilename

        return FileItemRegistry(basePath.resolve(key))
    }

    private val URL.registryFilename: String
        get() = host.filterForPath() + file.filterForPath() + ".processed"
}

class FileItemRegistry(private val path: Path) : ItemRegistry {
    private val registry = mutableSetOf<String>()

    init {
        if (Files.exists(path)) {
            registry.addAll(Files.readAllLines(path))
        }
    }

    override fun contains(item: Item): Boolean {
        val identity = item.persistableIdentity
        return registry.contains(identity)
    }

    override fun add(item: Item) {
        val identity = item.persistableIdentity

        registry.add(identity)

        Files.write(
            path,
            listOf(identity),
            StandardOpenOption.CREATE,
            StandardOpenOption.APPEND,
        )
    }
}

internal val Item.persistableIdentity: String
    get() =
        when {
            guid.isNotBlank() -> guid
            link.isNotBlank() -> link
            else -> title
        }
