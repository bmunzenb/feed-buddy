package com.munzenberger.feed.config

import java.io.InputStream
import java.io.OutputStream
import java.nio.file.Path

interface ConfigAdapter {
    fun read(file: Path): OperatorConfig

    fun read(inStream: InputStream): OperatorConfig

    fun write(
        config: OperatorConfig,
        file: Path,
    )

    fun write(
        config: OperatorConfig,
        outStream: OutputStream,
    )
}
