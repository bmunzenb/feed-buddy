package com.munzenberger.feed.config

import com.munzenberger.feed.handler.ItemHandler
import kotlin.reflect.KMutableProperty
import kotlin.reflect.full.memberProperties

class ItemHandlerFactory(private val registry: MutableMap<String, ItemHandler> = mutableMapOf()) {

    fun getInstance(config: ItemHandlerConfig): ItemHandler {

        return when {
            config.ref != null ->
                registry[config.ref] ?: throw IllegalArgumentException("Item handler with name ${config.ref} not found.")

            config.type != null ->
                newItemHandler(config).also {
                    if (config.name != null) {
                        registry[config.name] = it
                    }
                }

            else ->
                throw IllegalArgumentException("An item handler must define either a 'type' or a 'ref'.")
        }
    }

    companion object {

        internal fun newItemHandler(config: ItemHandlerConfig): ItemHandler {

            val clazz = Class.forName(config.type)
            val handler = clazz.getConstructor().newInstance() as ItemHandler

            val properties = handler::class.memberProperties.filterIsInstance<KMutableProperty<*>>()

            config.properties.forEach { (name, value) ->

                when (val property = properties.firstOrNull { it.name == name }) {
                    null -> throw IllegalArgumentException("Item handler ${config.type} does not have a settable property named '$name'.")
                    else -> property.setter.call(handler, value)
                }
            }

            return handler
        }
    }
}
