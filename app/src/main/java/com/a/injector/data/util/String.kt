package com.a.injector.data.util

fun String.shellQuote() = "'" + replace("'", "'\\''") + "'"