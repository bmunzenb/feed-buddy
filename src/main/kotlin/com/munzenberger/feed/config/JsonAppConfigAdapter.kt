package com.munzenberger.feed.config

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okio.Okio
import okio.Sink
import okio.Source
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

object JsonAppConfigAdapter : AppConfigAdapter {

    private val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

    private val adapter = moshi.adapter(AppConfig::class.java)

    override fun read(file: File): AppConfig {
        val source = Okio.source(file)
        return read(source)
    }

    override fun read(inStream: InputStream): AppConfig {
        val source = Okio.source(inStream)
        return read(source)
    }

    private fun read(source: Source): AppConfig {
        val bufferedSource = Okio.buffer(source)
        return adapter.fromJson(bufferedSource) ?: throw IOException("Could not read configuration")
    }

    override fun write(config: AppConfig, file: File) {
        val sink = Okio.sink(file)
        write(config, sink)
    }

    override fun write(config: AppConfig, outStream: OutputStream) {
        val sink = Okio.sink(outStream)
        write(config, sink)
    }

    private fun write(config: AppConfig, sink: Sink) {
        val bufferedSink = Okio.buffer(sink)
        bufferedSink.use { adapter.toJson(bufferedSink, config) }
    }
}
