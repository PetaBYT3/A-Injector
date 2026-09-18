package com.a.injector.domain.model

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable

data class StaticModel<T>(
    val id: T,
    val leadingContent: @Composable (() -> Unit)? = null,
    @StringRes val overlineTextResId: Int? = null,
    @StringRes val contentTextResId: Int,
    @StringRes val supportingTextResId: Int? = null,
    val trailingContent: @Composable (() -> Unit)? = null
)
