package com.a.injector.presentation.panel

import com.a.injector.R
import com.a.injector.domain.model.StaticModel

enum class PanelPermissionId {
    PendingRequest, Contributor
}

val panelPermissionItems = listOf(
    StaticModel(
        id = PanelPermissionId.PendingRequest,
        contentTextResId = R.string.item_pending_request
    ),
    StaticModel(
        id = PanelPermissionId.Contributor,
        contentTextResId = R.string.item_contributor
    )
)