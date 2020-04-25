package com.munzenberger.feed.handler

import com.munzenberger.feed.Item
import com.munzenberger.feed.URLClient
import okio.Okio
import java.io.File
import java.io.IOException
import java.net.URL
import java.net.URLDecoder
import java.text.NumberFormat

class DownloadEnclosures : ItemHandler {

    var targetDirectory: String = "."

    override fun execute(item: Item) {
        item.enclosures.forEach { enclosure ->

            val source = URL(enclosure.url)
            val target = targetFileFor(source)

            print("Downloading $source to $target... ")

            val result = profile { download(source, target) }

            println("${result.first.formatAsSize()} transferred in ${result.second.formatAsTime()}.")
        }
    }

    internal fun targetFileFor(source: URL): File {

        val filename = source.path
                .urlDecode()
                .split('/')
                .last()

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

private fun String.urlDecode(encoding: String = "UTF-8"): String {
    // handles nested URLs
    return when (val decoded = URLDecoder.decode(this, encoding)) {
        this -> this
        else -> decoded.urlDecode(encoding)
    }
}

private fun download(source: URL, target: File): Long {

    val inStream = URLClient.connect(source).inStream
    val inputSource = Okio.buffer(Okio.source(inStream))
    val outputSink = Okio.buffer(Okio.sink(target))

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
