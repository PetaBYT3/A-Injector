package com.a.injector.domain.model

import androidx.compose.runtime.Composable

data class StaticModel<T>(
    val id: T,
    val leadingContent: @Composable (() -> Unit)? = null,
    val overlineContent: Int? = null,
    val content: Int,
    val supportingContent: Int? = null,
    val trailingContent: @Composable (() -> Unit)? = null
)
