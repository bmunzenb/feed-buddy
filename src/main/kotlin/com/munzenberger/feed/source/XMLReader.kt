package com.munzenberger.feed.source

import java.io.InputStream
import java.io.InputStreamReader
import java.io.Reader

class XMLReader(inStream: InputStream, encoding: String) : Reader() {

    private val reader = XMLFilterReader(InputStreamReader(inStream, encoding), encoding)

    override fun close() {
        reader.close()
    }

    override fun read(cbuf: CharArray, off: Int, len: Int): Int {
        return reader.read(cbuf, off, len)
    }
}
