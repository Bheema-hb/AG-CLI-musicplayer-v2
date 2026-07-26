package com.harmonicplayer.app.util

import org.junit.Assert.assertEquals
import org.junit.Test

class TimeUtilsTest {

    @Test
    fun formatDuration_zeroOrNegative_returnsDefault() {
        assertEquals("00:00", formatDuration(0L))
        assertEquals("00:00", formatDuration(-500L))
    }

    @Test
    fun formatDuration_standardValues_returnsFormattedString() {
        assertEquals("03:45", formatDuration(225000L))
        assertEquals("01:05", formatDuration(65000L))
        assertEquals("00:09", formatDuration(9000L))
        assertEquals("10:00", formatDuration(600000L))
    }
}
