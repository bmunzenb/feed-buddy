package com.munzenberger.feed.config

import com.fasterxml.jackson.annotation.JsonInclude
import tools.jackson.databind.ObjectMapper
import tools.jackson.databind.SerializationFeature
import tools.jackson.dataformat.xml.XmlMapper
import tools.jackson.dataformat.yaml.YAMLMapper
import tools.jackson.module.kotlin.jacksonMapperBuilder
import tools.jackson.module.kotlin.kotlinModule
import tools.jackson.module.kotlin.readValue
import java.io.InputStream
import java.io.OutputStream
import java.nio.file.Files
import java.nio.file.Path

private val nonEmptyInclusion =
    JsonInclude.Value.construct(JsonInclude.Include.NON_EMPTY, JsonInclude.Include.NON_EMPTY)

abstract class JacksonConfigAdapter : ConfigAdapter {
    protected abstract val objectMapper: ObjectMapper

    override fun read(file: Path): OperatorConfig = Files.newInputStream(file).use { read(it) }

    override fun read(inStream: InputStream): OperatorConfig = inStream.use { objectMapper.readValue(it) }

    override fun write(
        config: OperatorConfig,
        file: Path,
    ) {
        Files.newOutputStream(file).use { write(config, it) }
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
        jacksonMapperBuilder()
            .enable(SerializationFeature.INDENT_OUTPUT)
            .changeDefaultPropertyInclusion { nonEmptyInclusion }
            .build()
}

object XmlConfigAdapter : JacksonConfigAdapter() {
    override val objectMapper: ObjectMapper =
        XmlMapper.builder()
            .defaultUseWrapper(false)
            .enable(SerializationFeature.INDENT_OUTPUT)
            .addModule(kotlinModule())
            .build()
}

object YamlConfigAdapter : JacksonConfigAdapter() {
    override val objectMapper: ObjectMapper =
        YAMLMapper.builder()
            .changeDefaultPropertyInclusion { nonEmptyInclusion }
            .addModule(kotlinModule())
            .build()
}
