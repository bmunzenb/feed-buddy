package com.munzenberger.feed.engine

import com.munzenberger.feed.Item
import com.munzenberger.feed.filterForPath
import com.munzenberger.feed.replaceAll
import java.net.URL
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardOpenOption

class FileItemRegistryFactory(private val basePath: Path) : ItemRegistryFactory {
    override fun getInstance(url: URL): ItemRegistry {
        return FileItemRegistry(basePath.resolve(url.registryFilename))
    }
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
            StandardOpenOption.APPEND
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

private val URL.registryFilename: String
    get() = host.filterForPath() + file.filterForPath() + ".processed"
