package com.munzenberger.feed

import java.text.NumberFormat

fun Long.formatAsTime(): String {
    val seconds = this / 1000 % 60
    val minutes = this / 1000 / 60

    return when {
        minutes > 0 -> "$minutes min $seconds sec"
        seconds > 0 -> "$seconds sec"
        else -> "$this ms"
    }
}

fun Long.formatAsSize(): String {
    val format =
        NumberFormat.getInstance().apply {
            minimumFractionDigits = 1
            maximumFractionDigits = 2
        }

    val mbs = this.toDouble() / 1024.0 / 1024.0

    return format.format(mbs) + " MB"
}
