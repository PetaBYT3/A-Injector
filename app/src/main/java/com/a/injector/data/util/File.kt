package com.a.injector.data.util

import java.util.Locale

fun Long.toMegaBytes(): String {
    val megabytes = this.toDouble() / (1024 * 1024)
    return String.format(Locale.getDefault(), "%.2f MB", megabytes)
}