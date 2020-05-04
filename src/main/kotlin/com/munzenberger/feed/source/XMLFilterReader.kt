package com.munzenberger.feed.source

import java.io.FilterReader
import java.io.InputStream
import java.io.InputStreamReader
import java.io.Reader
import java.lang.UnsupportedOperationException

class XMLFilterReader(
        inStream: InputStream,
        private val encoding: String
) : FilterReader(InputStreamReader(inStream, encoding)) {

    companion object {
        private const val BYTE_ORDER_MARK = '\uFEFF'
    }

    private var firstChar: Boolean = true
    private var contentStarted: Boolean = false

    override fun read(): Int {
        throw UnsupportedOperationException()
    }

    override fun reset() {
        throw UnsupportedOperationException()
    }

    override fun markSupported(): Boolean {
        return false
    }

    override fun mark(readAheadLimit: Int) {
        throw UnsupportedOperationException()
    }

    override fun read(cbuf: CharArray, off: Int, len: Int): Int {
        val read = super.read(cbuf, off, len)

        if (read < 0) {
            return read
        }

        var pointer = off
        var counter = 0

        for (i in off until off + read) {

            val c = cbuf[i]

            if (firstChar) {
                firstChar = false
                // strip the UTF-8 byte order mark, if present
                if (encoding == "UTF-8" && c == BYTE_ORDER_MARK) {
                    continue
                }
            }

            if (!contentStarted) {
                // trim any leading whitespace
                if (c.isWhitespace()) {
                    continue
                }
                contentStarted = true
            }

            if (c.isValidXML()) {
                cbuf[pointer++] = c
                counter++
            }
        }

        return counter
    }
}

private fun Char.isValidXML(): Boolean = toInt().let {
    // https://www.w3.org/TR/xml/#charsets
    it == 0x9 ||
            it == 0xA ||
            it == 0xD ||
            it in 0x20..0xD7FF ||
            it in 0xE000..0xFFFD ||
            it in 0x10000..0x10FFFF
}
