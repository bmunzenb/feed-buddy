package com.munzenberger.feed.app

import com.munzenberger.feed.Logger
import java.io.File
import java.io.PrintWriter
import java.io.StringWriter

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
