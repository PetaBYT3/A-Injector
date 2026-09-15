package com.a.injector.domain.model

import androidx.compose.runtime.Composable

data class StaticMenu(
    val onClick: (() -> Unit)? = null,
    val leadingContent: @Composable (() -> Unit)? = null,
    val overlineTextResId: Int? = null,
    val titleResId: Int,
    val supportingTextResId: Int? = null,
    val trailingContent: @Composable (() -> Unit)? = null
)
