package com.munzenberger.feed

interface Logger {
    fun print(obj: Any)
    fun println()
    fun println(obj: Any)
    fun printStackTrace(t: Throwable)
}

class CompositeLogger : Logger {

    private val loggers = mutableListOf<Logger>()

    fun add(logger: Logger) {
        loggers.add(logger)
    }

    override fun print(obj: Any) {
        loggers.forEach { it.print(obj) }
    }

    override fun println() {
        loggers.forEach { it.println() }
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

    override fun println() {
        kotlin.io.println()
    }

    override fun println(obj: Any) {
        kotlin.io.println(obj)
    }

    override fun printStackTrace(t: Throwable) {
        t.printStackTrace(System.err)
    }
}