package com.munzenberger.feed.config

import java.io.File
import java.io.InputStream
import java.io.OutputStream

interface AppConfigAdapter {

    fun read(file: File): AppConfig

    fun read(inStream: InputStream): AppConfig

    fun write(config: AppConfig, file: File)

    fun write(config: AppConfig, outStream: OutputStream)
}
