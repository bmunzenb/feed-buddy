package com.munzenberger.feed

import java.io.File
import java.io.PrintWriter
import java.io.StringWriter

interface Logger {
    fun print(obj: Any)
    fun println(obj: Any)
    fun printStackTrace(t: Throwable)

    fun format(format: String, vararg params: Any) {
        val str = String.format(format, *params)
        print(str)
    }

    fun formatln(format: String, vararg params: Any) {
        val str = String.format(format, *params)
        println(str)
    }
}