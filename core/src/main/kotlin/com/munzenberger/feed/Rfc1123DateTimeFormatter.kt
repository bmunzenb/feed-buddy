package com.munzenberger.feed

import java.time.chrono.IsoChronology
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder
import java.time.format.ResolverStyle
import java.time.format.SignStyle
import java.time.format.TextStyle
import java.time.temporal.ChronoField

// Workaround for RFC_1123_DATE_TIME not supporting time zone strings other than GMT
// https://stackoverflow.com/questions/45829799/java-time-format-datetimeformatter-rfc-1123-date-time-fails-to-parse-time-zone-n
@Suppress("MagicNumber")
internal fun rfc1123DateTimeFormatter(): DateTimeFormatter {
    // custom map for days of week
    val dow =
        mapOf(
            1L to "Mon",
            2L to "Tue",
            3L to "Wed",
            4L to "Thu",
            5L to "Fri",
            6L to "Sat",
            7L to "Sun",
        )

    // custom map for months
    val moy =
        mapOf(
            1L to "Jan",
            2L to "Feb",
            3L to "Mar",
            4L to "Apr",
            5L to "May",
            6L to "Jun",
            7L to "Jul",
            8L to "Aug",
            9L to "Sep",
            10L to "Oct",
            11L to "Nov",
            12L to "Dec",
        )

    // create with same format as RFC_1123_DATE_TIME
    return DateTimeFormatterBuilder()
        .parseCaseInsensitive()
        .parseLenient()
        .optionalStart()
        .appendText(ChronoField.DAY_OF_WEEK, dow)
        .appendLiteral(", ")
        .optionalEnd()
        .appendValue(ChronoField.DAY_OF_MONTH, 1, 2, SignStyle.NOT_NEGATIVE)
        .appendLiteral(' ')
        .appendText(ChronoField.MONTH_OF_YEAR, moy)
        .appendLiteral(' ')
        .appendValue(ChronoField.YEAR, 4) // 2 digit year not handled
        .appendLiteral(' ')
        .appendValue(ChronoField.HOUR_OF_DAY, 2)
        .appendLiteral(':')
        .appendValue(ChronoField.MINUTE_OF_HOUR, 2)
        .optionalStart()
        .appendLiteral(':')
        .appendValue(ChronoField.SECOND_OF_MINUTE, 2)
        .optionalEnd()
        .appendLiteral(' ') // difference from RFC_1123_DATE_TIME: optional offset OR zone ID
        .optionalStart()
        .appendZoneText(TextStyle.SHORT)
        .optionalEnd()
        .optionalStart()
        .appendOffset("+HHMM", "GMT") // use the same resolver style and chronology
        .toFormatter()
        .withResolverStyle(ResolverStyle.SMART)
        .withChronology(IsoChronology.INSTANCE)
}
