package com.munzenberger.feed

import java.io.File
import java.util.TimerTask

class ConfigurationChangeTask(private val file: File, private val block: () -> Unit) : TimerTask() {

    private val timestamp = file.lastModified()

    override fun run() {
        if (file.lastModified() != timestamp) {
            block.invoke()
        }
    }
}
