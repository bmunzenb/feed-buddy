package com.munzenberger.feed.engine

import com.munzenberger.feed.Item
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardOpenOption

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

        Files.write(path, listOf(identity),
                StandardOpenOption.CREATE,
                StandardOpenOption.APPEND)
    }
}

private val Item.persistableIdentity: String
        get() = guid
