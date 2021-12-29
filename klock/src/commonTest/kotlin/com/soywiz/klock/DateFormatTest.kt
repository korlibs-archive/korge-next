package com.soywiz.klock

import kotlin.test.*

class DateFormatTest {
    @Test
    fun test() {
        assertEquals("Sat, 03 Dec 2011 10:15:30 GMT+0100", DateFormat.FORMAT1.parse("2011-12-03T10:15:30+01:00").toStringDefault())
        assertEquals("Sat, 03 Dec 2011 10:15:30 GMT+0100", DateFormat.FORMAT1.parse("2011-12-03T10:15:30+0100").toStringDefault())
    }

    @Test
    fun testTimeStampFormatting() {
        val timeStamp01012020 = 1577836800000
        val formattedTimeStamp = DateFormat("YYYY-MM-dd hh:mm:ss").format(timeStamp01012020)

        assertEquals("2020-01-01 00:00:00", formattedTimeStamp)
    }
}
