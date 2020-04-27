package com.munzenberger.feed.config

import kotlin.reflect.KMutableProperty
import kotlin.reflect.full.memberProperties

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

    override fun reset() {
        registry.clear()
    }

    companion object {

        @Suppress("UNCHECKED_CAST")
        internal fun <T> newItemProcessor(config: ItemProcessorConfig): T {

            val clazz = Class.forName(config.type)
            val process = clazz.getConstructor().newInstance()

            val properties = process!!::class.memberProperties.filterIsInstance<KMutableProperty<*>>()

            config.properties.forEach { (name, value) ->

                when (val property = properties.firstOrNull { it.name == name }) {
                    null -> throw IllegalArgumentException("Item processor ${config.type} does not have a settable property named '$name'.")
                    else -> try {
                        property.setter.call(process, value)
                    } catch (e: Throwable) {
                        throw IllegalArgumentException("[${config.type}] Could not set property \"$name\" to value \"$value\" of type ${value::class.simpleName}", e)
                    }
                }
            }

            return process as T
        }
    }
}
