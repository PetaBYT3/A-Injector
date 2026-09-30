package com.a.injector.domain.model

data class StaticModel<T>(
    val id: T,
    val leadingContent: (() -> Unit)? = null,
    val overlineContent: Int? = null,
    val content: Int,
    val supportingContent: Int? = null,
    val trailingContent: (() -> Unit)? = null
)
