package com.munzenberger.feed.config

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okio.Okio
import okio.Source
import java.io.File
import java.io.IOException
import java.io.InputStream

object ConfigParser {

    fun parse(file: File): AppConfig {

        val source = Okio.source(file)
        return parse(source)
    }

    fun parse(inStream: InputStream): AppConfig {

        val source = Okio.source(inStream)
        return parse(source)
    }

    private fun parse(source: Source): AppConfig {

        val moshi = Moshi.Builder()
                .add(KotlinJsonAdapterFactory())
                .build()

        val adapter = moshi.adapter(AppConfig::class.java)
        val bufferedSource = Okio.buffer(source)

        return adapter.fromJson(bufferedSource) ?: throw IOException("Could not parse configuration")
    }
}
