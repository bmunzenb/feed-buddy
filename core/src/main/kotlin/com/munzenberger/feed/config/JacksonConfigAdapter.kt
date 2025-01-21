package com.munzenberger.feed.config

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import java.io.File
import java.io.InputStream
import java.io.OutputStream

abstract class JacksonConfigAdapter : ConfigAdapter {
    protected abstract val objectMapper: ObjectMapper

    override fun read(file: File): OperatorConfig = objectMapper.readValue(file)

    override fun read(inStream: InputStream): OperatorConfig = inStream.use { objectMapper.readValue(it) }

    override fun write(
        config: OperatorConfig,
        file: File,
    ) {
        objectMapper.writeValue(file, config)
    }

    override fun write(
        config: OperatorConfig,
        outStream: OutputStream,
    ) {
        outStream.use { objectMapper.writeValue(it, config) }
    }
}

object JsonConfigAdapter : JacksonConfigAdapter() {
    override val objectMapper: ObjectMapper =
        jacksonObjectMapper().apply {
            enable(SerializationFeature.INDENT_OUTPUT)
            setSerializationInclusion(JsonInclude.Include.NON_EMPTY)
        }
}

object XmlConfigAdapter : JacksonConfigAdapter() {
    override val objectMapper: ObjectMapper

    init {
        val module =
            JacksonXmlModule().apply {
                setDefaultUseWrapper(false)
            }
        objectMapper =
            XmlMapper(module).registerKotlinModule().apply {
                enable(SerializationFeature.INDENT_OUTPUT)
            }
    }
}

object YamlConfigAdapter : JacksonConfigAdapter() {
    override val objectMapper: ObjectMapper =
        ObjectMapper(YAMLFactory()).apply {
            setSerializationInclusion(JsonInclude.Include.NON_EMPTY)
            registerKotlinModule()
        }
}
