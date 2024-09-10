package com.shalatan.audioplayground.utils

import java.util.Locale

fun Int.formatMilliSecondsToMinutes(): String {
    val totalSeconds = this.div(1000)
    val minutes = totalSeconds.div(60)
    val seconds = totalSeconds.rem(60)
    return String.format(Locale.ENGLISH, "%d:%02d", minutes, seconds)
}