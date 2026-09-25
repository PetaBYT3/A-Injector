package com.a.injector.presentation.hero

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.Icon
import com.a.injector.R
import com.a.injector.domain.model.StaticModel

enum class SkinAction {
    Edit, AddReplace
}

val skinActions = listOf(
    StaticModel(
        id = SkinAction.Edit,
        leadingContent = { Icon(Icons.Rounded.Edit, null) },
        contentTextResId = R.string.action_edit
    ),
    StaticModel(
        id = SkinAction.AddReplace,
        leadingContent = { Icon(Icons.Rounded.Add, null) },
        contentTextResId = R.string.action_add
    )
)