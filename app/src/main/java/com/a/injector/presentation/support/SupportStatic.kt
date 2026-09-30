package com.a.injector.presentation.support

import com.a.injector.R
import com.a.injector.domain.model.StaticModel

enum class SupportStatus {
    Current, Requested
}

val supportStatuses = listOf(
    StaticModel(
        id = SupportStatus.Current,
        content = R.string.current
    ),
    StaticModel(
        id = SupportStatus.Requested,
        content = R.string.requested
    )
)