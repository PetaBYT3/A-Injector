package com.a.injector.presentation.role

import com.a.injector.R
import com.a.injector.domain.model.StaticModel

enum class RoleStatus {
    Current, Requested
}

val roleStatuses = listOf(
    StaticModel(
        id = RoleStatus.Current,
        content = R.string.current
    ),
    StaticModel(
        id = RoleStatus.Requested,
        content = R.string.requested
    )
)