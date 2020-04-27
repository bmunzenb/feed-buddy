package com.munzenberger.feed.config

import kotlin.reflect.KMutableProperty
import kotlin.reflect.full.memberProperties

interface ItemProcessorFactory<out T> {
    fun getInstance(config: ItemProcessorConfig): T
}

class DefaultItemProcessorFactory<T>(private val registry: MutableMap<String, T> = mutableMapOf()) : ItemProcessorFactory<T> {

    override fun getInstance(config: ItemProcessorConfig): T = when {

        config.ref != null ->
            registry[config.ref] ?: throw IllegalArgumentException("Item processor with name '${config.ref}' not found.")

        config.type != null ->
            newItemProcessor<T>(config).also {
                if (config.name != null) {
                    registry[config.name] = it
                }
            }

        else ->
            throw IllegalArgumentException("An item processor must define either a 'type' or a 'ref'.")
    }

    companion object {

        @Suppress("UNCHECKED_CAST")
        internal fun <T> newItemProcessor(config: ItemProcessorConfig): T {

            val clazz = Class.forName(config.type)
            val process = clazz.getConstructor().newInstance()

            val properties = process!!::class.memberProperties.filterIsInstance<KMutableProperty<*>>()

            config.properties.forEach { (name, value) ->

                when (val property = properties.firstOrNull { it.name == name }) {
                    null -> throw IllegalArgumentException("Item process ${config.type} does not have a settable property named '$name'.")
                    else -> property.setter.call(process, value)
                }
            }

            return process as T
        }
    }
}
