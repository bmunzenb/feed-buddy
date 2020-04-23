package com.munzenberger.feed.source

import java.io.InputStream
import java.io.InputStreamReader
import java.io.Reader

object XMLInputStreamDecoder {
    fun decode(inStream: InputStream, encoding: String): Reader {

        // decodes stream
        val reader: Reader = InputStreamReader(inStream, encoding)

        // cleans up the stream for XML
        return XMLFilterReader(reader, encoding)
    }
}
