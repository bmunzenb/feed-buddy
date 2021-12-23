package com.munzenberger.feed.handler

import com.munzenberger.feed.FeedContext
import com.munzenberger.feed.Item
import com.munzenberger.feed.URLClient
import com.munzenberger.feed.config.filteredForPath
import com.munzenberger.feed.filename
import okio.buffer
import okio.sink
import okio.source
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.net.URL
import java.net.URLDecoder
import java.text.NumberFormat

class DownloadEnclosures : ItemHandler {

    var targetDirectory: String = "."

    override fun execute(context: FeedContext, item: Item) {
        item.enclosures.forEach { enclosure ->

            print("Resolving enclosure source... ")

            URLClient.connect(URL(enclosure.url)).run {

                println(resolvedUrl)

                contentDisposition?.let {
                    println("Content-Disposition: $it")
                }

                val target = targetFileFor(filename)

                print("Downloading to $target... ")

                val result = profile { download(inStream, target) }

                println("${result.first.formatAsSize()} transferred in ${result.second.formatAsTime()}.")

                item.timestampAsInstant?.let {
                    if (!target.setLastModified(it.toEpochMilli())) {
                        System.err.println("Could not set last modified time on file: $target")
                    }
                }
            }
        }
    }

    internal fun targetFileFor(filename: String): File {

        var path = targetDirectory + File.separator + filename

        var targetFile = File(path)

        if (targetFile.exists()) {

            // insert a timestamp into the filename to make it unique

            var name = path
            var extension = ""

            val i = path.lastIndexOf('.')
            if (i > 0) {
                name = path.substring(0, i)
                extension = path.substring(i)
            }

            path = name + "-" + System.currentTimeMillis() + extension

            targetFile = File(path)

            if (targetFile.exists()) {
                throw IOException("Local file already exists: $targetFile")
            }
        }

        return targetFile
    }
}

internal val URLClient.Response.filename: String
    // use the filename from the content disposition header, if present
    get() = contentDisposition.filename?.filteredForPath() ?: resolvedUrl.filename

internal val URL.filename: String
    get() = this.path.urlDecode().split('/').last()

private fun String.urlDecode(encoding: String = "UTF-8"): String {
    // handles nested URLs
    return when (val decoded = URLDecoder.decode(this, encoding)) {
        this -> this
        else -> decoded.urlDecode(encoding)
    }
}

private fun download(inStream: InputStream, target: File): Long {

    val inputSource = inStream.source().buffer()
    val outputSink = target.sink().buffer()

    return inputSource.use { input ->
        outputSink.use { output ->
            input.readAll(output)
        }
    }
}

private fun <T> profile(block: () -> T): Pair<T, Long> {
    val start = System.currentTimeMillis()
    val v = block.invoke()
    val time = System.currentTimeMillis() - start
    return v to time
}

fun Long.formatAsTime(): String {

    val seconds = this / 1000 % 60
    val minutes = this / 1000 / 60

    return when {
        minutes > 0 -> "$minutes min $seconds sec"
        seconds > 0 -> "$seconds sec"
        else -> "$this ms"
    }
}

fun Long.formatAsSize(): String {

    val format = NumberFormat.getInstance().apply {
        minimumFractionDigits = 1
        maximumFractionDigits = 2
    }

    val mbs = this.toDouble() / 1024.0 / 1024.0

    return format.format(mbs) + " MB"
}