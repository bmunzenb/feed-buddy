package com.munzenberger.feed

fun String.filterForPath() =
    replaceAll(
        replaceChars = "<>:\"/\\|?*=",
        with = '-',
    )

fun String.replaceAll(
    replaceChars: String,
    with: Char,
): String {
    val sb = StringBuilder(this)
    for (i in sb.indices) {
        if (sb[i] in replaceChars) {
            sb[i] = with
        }
    }
    return sb.toString()
}
