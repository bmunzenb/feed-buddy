package com.munzenberger.feed.handler

import com.munzenberger.feed.FeedContext
import com.munzenberger.feed.Item
import com.munzenberger.feed.ItemProcessorEvent
import com.munzenberger.feed.client.Response
import com.munzenberger.feed.client.URLClient
import com.munzenberger.feed.filterForPath
import com.munzenberger.feed.formatAsSize
import com.munzenberger.feed.formatAsTime
import com.munzenberger.feed.formatln
import com.munzenberger.feed.print
import com.munzenberger.feed.println
import okio.buffer
import okio.sink
import okio.source
import java.io.IOException
import java.io.InputStream
import java.net.URI
import java.net.URL
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.attribute.FileTime
import java.util.function.Consumer

class DownloadEnclosures : ItemHandler {
    var targetDirectory: String = "."
    var parameters: Map<String, String> = emptyMap()

    override fun execute(
        context: FeedContext,
        item: Item,
        eventConsumer: Consumer<ItemProcessorEvent>,
    ) {
        item.enclosures.forEach { enclosure ->
            eventConsumer.print("Resolving enclosure source... ")

            URLClient().connect(enclosure.url.withQueryParameters(parameters).toURL()).run {
                eventConsumer.println(resolvedUrl)

                executeForResponse(this, item, eventConsumer)
            }
        }
    }

    private fun executeForResponse(
        response: Response,
        item: Item,
        eventConsumer: Consumer<ItemProcessorEvent>,
    ) {
        response.contentDisposition.value?.let {
            eventConsumer.println("Content-Disposition: $it")
        }

        val target = targetFileFor(response.filename)

        eventConsumer.print("Downloading to $target... ")

        val result = profile { download(response.inStream, target) }

        eventConsumer.formatln(
            "%s transferred in %s.",
            result.first.formatAsSize(),
            result.second.formatAsTime(),
        )

        item.timestampAsInstant?.let {
            try {
                Files.setLastModifiedTime(target, FileTime.fromMillis(it.toEpochMilli()))
            } catch (e: IOException) {
                eventConsumer.println("Could not set last modified time on file: $target")
            }
        }
    }

    internal fun targetFileFor(filename: String): Path {
        val targetDir = Paths.get(targetDirectory).toAbsolutePath().normalize()
        var targetFile = targetDir.resolveWithinDir(filename)

        if (Files.exists(targetFile)) {
            // insert a timestamp into the filename to make it unique

            var name = filename
            var extension = ""

            val i = filename.lastIndexOf('.')
            if (i > 0) {
                name = filename.substring(0, i)
                extension = filename.substring(i)
            }

            val uniqueFilename = name + "-" + System.currentTimeMillis() + extension

            targetFile = targetDir.resolveWithinDir(uniqueFilename)

            if (Files.exists(targetFile)) {
                throw IOException("Local file already exists: $targetFile")
            }
        }

        return targetFile
    }
}

private fun Path.resolveWithinDir(filename: String): Path {
    val resolved = resolve(filename).normalize()

    if (resolved.parent != this) {
        throw IOException("Filename resolves outside of the target directory: $filename")
    }

    return resolved
}

internal fun String.withQueryParameters(parameters: Map<String, String>): URI {
    if (parameters.isEmpty()) return URI.create(this)

    val encoded =
        parameters.entries.joinToString("&") { (k, v) ->
            "${URLEncoder.encode(k, "UTF-8")}=${URLEncoder.encode(v, "UTF-8")}"
        }

    val separator = if (URI.create(this).rawQuery == null) "?" else "&"

    return URI.create("$this$separator$encoded")
}

internal val Response.filename: String
    // use the filename from the content disposition header, if present
    get() = contentDisposition.filename?.filterForPath() ?: resolvedUrl.filename

internal val URL.filename: String
    get() = this.path.urlDecode().substringAfterLast('/')

private fun String.urlDecode(encoding: String = "UTF-8"): String {
    // handles nested URLs
    return when (val decoded = URLDecoder.decode(this, encoding)) {
        this -> this
        else -> decoded.urlDecode(encoding)
    }
}

private fun download(
    inStream: InputStream,
    target: Path,
): Long {
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
