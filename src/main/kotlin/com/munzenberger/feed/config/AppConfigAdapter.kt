package com.munzenberger.feed.config

import java.io.File
import java.io.InputStream
import java.io.OutputStream

interface AppConfigAdapter {

    fun read(file: File): AppConfig {
        return file.inputStream().use { read(it) }
    }

    fun read(inStream: InputStream): AppConfig

    fun write(config: AppConfig, file: File) {
        file.outputStream().use { write(config, it) }
    }

    fun write(config: AppConfig, outStream: OutputStream)
}
