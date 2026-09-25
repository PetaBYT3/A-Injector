package com.a.injector.presentation.home

import androidx.compose.material3.Icon
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import com.a.injector.R
import com.a.injector.domain.model.StaticModel

enum class AboutDeveloper {
    Github, Support
}

val AboutDevelopers = listOf(
    StaticModel(
        id = AboutDeveloper.Github,
        leadingContent = { Icon(ImageVector.vectorResource(R.drawable.github), null) },
        contentTextResId = R.string.item_github
    ),
    StaticModel(
        id = AboutDeveloper.Support,
        leadingContent = { Icon(ImageVector.vectorResource(R.drawable.support), null) },
        contentTextResId = R.string.item_support
    )
)