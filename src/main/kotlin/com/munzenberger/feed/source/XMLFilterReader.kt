package com.munzenberger.feed.source

import java.io.FilterReader
import java.io.Reader
import java.lang.UnsupportedOperationException

class XMLFilterReader(inReader: Reader, private val encoding: String) : FilterReader(inReader) {

    companion object {
        private const val BYTE_ORDER_MARK = '\uFEFF'

        private fun isValidXMLChar(c: Char) = c.toInt().let {
            // https://www.w3.org/TR/xml/#charsets
            it == 0x9 ||
                    it == 0xA ||
                    it == 0xD ||
                    it in 0x20..0xD7FF ||
                    it in 0xE000..0xFFFD ||
                    it in 0x10000..0x10FFFF
        }
    }

    private var first: Boolean = true

    override fun read(): Int {
        throw UnsupportedOperationException()
    }

    override fun read(cbuf: CharArray, off: Int, len: Int): Int {
        val read = super.read(cbuf, off, len)

        if (read < 0) {
            return read
        }

        var pointer = off
        var counter = 0
        var i = off

        while (i < off + read) {

            if (first) {
                first = false
                // strip the UTF-8 byte order mark, if present
                if (encoding == "UTF-8" && cbuf[i] == BYTE_ORDER_MARK) {
                    continue
                }
            }

            if (isValidXMLChar(cbuf[i])) {
                cbuf[pointer++] = cbuf[i]
                counter++
            }

            i++
        }

        return counter
    }
}
