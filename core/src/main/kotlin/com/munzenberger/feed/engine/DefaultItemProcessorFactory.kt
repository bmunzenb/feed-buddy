package com.munzenberger.feed.engine

import com.munzenberger.feed.config.ItemProcessorConfig
import kotlin.reflect.KMutableProperty
import kotlin.reflect.full.memberProperties

class DefaultItemProcessorFactory<T : ItemProcessor>(
    private val registry: MutableMap<String, T> = mutableMapOf(),
) : ItemProcessorFactory<T> {
    override fun getInstance(config: ItemProcessorConfig): T =
        when {
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

                val property =
                    properties.firstOrNull { it.name == name }
                        ?: throw IllegalArgumentException("Item processor ${config.type} does not have a settable property named '$name'.")

                val propertyType = property.returnType.classifier

                val coercedValue: Any =
                    when {
                        value::class == propertyType -> value
                        value is String && propertyType == Int::class -> value.toInt()
                        value is String && propertyType == Boolean::class -> value.toBoolean()
                        else -> throw IllegalArgumentException(
                            "Incompatible types: $value (${value::class}) cannot be set for property \"$name\" of type $propertyType.",
                        )
                    }

                try {
                    property.setter.call(process, coercedValue)
                } catch (e: Throwable) {
                    throw IllegalArgumentException(
                        "[${config.type}] Could not set property \"$name\" to value \"$value\" of type ${value::class.simpleName}",
                        e,
                    )
                }
            }

            return process as T
        }
    }
}
