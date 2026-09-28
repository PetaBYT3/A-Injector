package com.a.injector.presentation.supporting

import io.github.vinceglb.filekit.PlatformFile

data class SupportingState(
    val nominalTextField: String = "",
    val image: PlatformFile? = null,

    val isUpsertSupportingButtonLoading: Boolean = false
)
