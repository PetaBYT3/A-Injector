package com.a.injector.domain.model

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import kotlin.uuid.Uuid

data class StaticModel(
    val id: String = Uuid.random().toString(),
    val onClick: (() -> Unit)? = null,
    val leadingContent: @Composable (() -> Unit)? = null,
    @StringRes val overlineTextResId: Int? = null,
    @StringRes val contentTextResId: Int,
    @StringRes val supportingTextResId: Int? = null,
    val trailingContent: @Composable (() -> Unit)? = null
)
