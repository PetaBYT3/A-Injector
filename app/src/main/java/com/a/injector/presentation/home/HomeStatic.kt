package com.a.injector.presentation.home

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.OpenInNew
import androidx.compose.material3.Icon
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import com.a.injector.R
import com.a.injector.domain.model.StaticModel

enum class AboutDeveloper {
    Mlbb, Linkedin, Tiktok, Github, Support
}

val AboutDevelopers = listOf(
    StaticModel(
        id = AboutDeveloper.Mlbb,
        leadingContent = { Icon(ImageVector.vectorResource(R.drawable.mlbb), null) },
        contentTextResId = R.string.mlbb,
        supportingTextResId = R.string.mlbb_desc,
        trailingContent = { Icon(Icons.Rounded.ContentCopy, null) }
    ),
    StaticModel(
        id = AboutDeveloper.Linkedin,
        leadingContent = { Icon(ImageVector.vectorResource(R.drawable.linkedin), null) },
        contentTextResId = R.string.linkedin,
        supportingTextResId = R.string.linkedin_desc,
        trailingContent = { Icon(Icons.Rounded.OpenInNew, null) }
    ),
    StaticModel(
        id = AboutDeveloper.Tiktok,
        leadingContent = { Icon(ImageVector.vectorResource(R.drawable.tiktok), null) },
        contentTextResId = R.string.tiktok,
        supportingTextResId = R.string.tiktok_desc,
        trailingContent = { Icon(Icons.Rounded.OpenInNew, null) }
    ),
    StaticModel(
        id = AboutDeveloper.Github,
        leadingContent = { Icon(ImageVector.vectorResource(R.drawable.github), null) },
        contentTextResId = R.string.github,
        supportingTextResId = R.string.github_desc,
        trailingContent = { Icon(Icons.Rounded.OpenInNew, null) }
    ),
    StaticModel(
        id = AboutDeveloper.Support,
        leadingContent = { Icon(ImageVector.vectorResource(R.drawable.support), null) },
        contentTextResId = R.string.support,
        supportingTextResId = R.string.support_desc,
        trailingContent = { Icon(Icons.Rounded.OpenInNew, null) }
    )
)