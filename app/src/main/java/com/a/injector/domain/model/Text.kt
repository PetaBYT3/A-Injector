package com.a.injector.domain.model

import android.content.Context
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.util.fastForEach

sealed interface Text {
    data class Static(val value: String): Text
    class Resource(@StringRes val resId: Int): Text
    data class Combined(val texts: List<Text>): Text

    operator fun plus(other: Text): Text {
        return Combined(
            when {
                this is Combined && other is Combined -> this.texts + other.texts
                this is Combined -> this.texts + other
                other is Combined -> listOf(this) + other.texts
                else -> listOf(this, other)
            }
        )
    }

    @Composable
    fun asString(): String {
        return when (this) {
            is Static -> value
            is Resource -> stringResource(resId)
            is Combined -> buildString {
                texts.fastForEach { text ->
                    append(text.asString())
                }
            }
        }
    }

    fun asString(context: Context): String {
        return when (this) {
            is Static -> value
            is Resource -> context.getString(resId)
            is Combined -> texts.joinToString("") { it.asString(context) }
        }
    }
}