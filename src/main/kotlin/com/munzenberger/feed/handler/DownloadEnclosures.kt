package com.munzenberger.feed.handler

import com.munzenberger.feed.Item
import com.munzenberger.feed.URLClient
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

    override fun execute(item: Item) {
        item.enclosures.forEach { enclosure ->

            print("Resolving enclosure source... ")

            val response = URLClient.connect(URL(enclosure.url))

            println(response.resolvedUrl)

            // TODO can we determine the local filename by the 'content-disposition' response header?

            val target = targetFileFor(response.resolvedUrl)

            print("Downloading to $target... ")

            val result = profile { download(response.inStream, target) }

            // TODO set the last modified time on the local file

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
