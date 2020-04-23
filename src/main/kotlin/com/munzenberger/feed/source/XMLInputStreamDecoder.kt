package com.munzenberger.feed.source

import java.io.InputStream
import java.io.InputStreamReader
import java.io.Reader
import java.io.StringReader

object XMLInputStreamDecoder {
    fun decode(inStream: InputStream, encoding: String): Reader {

        // decodes stream
        var reader: Reader = InputStreamReader(inStream, encoding)

        // cleans up the stream for XML
        reader = XMLFilterReader(reader, encoding)

        // trim any whitespace
        var content = reader.readText()
        content = content.trim()

        return StringReader(content)
    }
}
