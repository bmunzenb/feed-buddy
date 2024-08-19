package com.munzenberger.feed

import java.io.File
import java.io.PrintWriter
import java.io.StringWriter

// TODO Refactor logger into a status callback
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

class CompositeLogger : Logger {

    private val loggers = mutableListOf<Logger>()

    fun add(logger: Logger) {
        loggers.add(logger)
    }

    override fun print(obj: Any) {
        loggers.forEach { it.print(obj) }
    }

    override fun println(obj: Any) {
        loggers.forEach { it.println(obj) }
    }

    override fun printStackTrace(t: Throwable) {
        loggers.forEach { it.printStackTrace(t) }
    }
}

object ConsoleLogger : Logger {

    override fun print(obj: Any) {
        kotlin.io.print(obj)
    }

    override fun println(obj: Any) {
        kotlin.io.println(obj)
    }

    override fun printStackTrace(t: Throwable) {
        t.printStackTrace(System.err)
    }
}

class FileLogger(val file: File) : Logger {

    companion object {
        private val NEWLINE = System.lineSeparator()
    }

    override fun print(obj: Any) {
        file.appendText(obj.toString())
    }

    override fun println(obj: Any) {
        file.appendText(obj.toString() + NEWLINE)
    }

    override fun printStackTrace(t: Throwable) {
        val sw = StringWriter()
        t.printStackTrace(PrintWriter(sw))
        file.appendText(sw.toString() + NEWLINE)
    }
}
