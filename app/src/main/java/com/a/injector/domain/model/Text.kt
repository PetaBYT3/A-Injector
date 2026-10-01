package com.a.injector.domain.model

import android.content.Context
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource

sealed interface Text {
    data class Static(val value: String): Text
    class Resource(@StringRes val resId: Int): Text

    @Composable
    fun asString(): String {
        return when (this) {
            is Static -> value
            is Resource -> stringResource(resId)
        }
    }

    fun asString(context: Context): String {
        return when (this) {
            is Static -> value
            is Resource -> context.getString(resId)
        }
    }
}