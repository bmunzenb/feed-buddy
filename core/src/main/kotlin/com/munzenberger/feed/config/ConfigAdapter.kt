package com.munzenberger.feed.config

import java.io.File
import java.io.InputStream
import java.io.OutputStream

interface ConfigAdapter {
    fun read(file: File): OperatorConfig

    fun read(inStream: InputStream): OperatorConfig

    fun write(
        config: OperatorConfig,
        file: File,
    )

    fun write(
        config: OperatorConfig,
        outStream: OutputStream,
    )
}
