package com.a.injector.presentation.util

import java.text.NumberFormat
import java.util.Locale

fun Long?.toIdr(): String {
    val localeID = Locale("id", "ID")
    val formatter = NumberFormat.getCurrencyInstance(localeID)
    return formatter.format(this)
}