package com.a.injector.presentation.home

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material.icons.rounded.OpenInNew
import androidx.compose.material3.Icon
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import com.a.injector.R
import com.a.injector.domain.model.StaticModel

enum class AboutDeveloper {
    Mlbb, Email, Linkedin, Tiktok, Github, Support
}

val AboutDevelopers = listOf(
    StaticModel(
        id = AboutDeveloper.Mlbb,
        leadingContent = { Icon(ImageVector.vectorResource(R.drawable.mlbb), null) },
        content = R.string.mlbb,
        supportingContent = R.string.mlbb_desc,
        trailingContent = { Icon(Icons.Rounded.ContentCopy, null) }
    ),
    StaticModel(
        id = AboutDeveloper.Email,
        leadingContent = { Icon(Icons.Rounded.Email, null) },
        content = R.string.dev_email,
        supportingContent = R.string.dev_email_desc,
        trailingContent = { Icon(Icons.Rounded.OpenInNew, null) }
    ),
    StaticModel(
        id = AboutDeveloper.Linkedin,
        leadingContent = { Icon(ImageVector.vectorResource(R.drawable.linkedin), null) },
        content = R.string.linkedin,
        supportingContent = R.string.linkedin_desc,
        trailingContent = { Icon(Icons.Rounded.OpenInNew, null) }
    ),
    StaticModel(
        id = AboutDeveloper.Tiktok,
        leadingContent = { Icon(ImageVector.vectorResource(R.drawable.tiktok), null) },
        content = R.string.tiktok,
        supportingContent = R.string.tiktok_desc,
        trailingContent = { Icon(Icons.Rounded.OpenInNew, null) }
    ),
    StaticModel(
        id = AboutDeveloper.Github,
        leadingContent = { Icon(ImageVector.vectorResource(R.drawable.github), null) },
        content = R.string.github,
        supportingContent = R.string.github_desc,
        trailingContent = { Icon(Icons.Rounded.OpenInNew, null) }
    ),
    StaticModel(
        id = AboutDeveloper.Support,
        leadingContent = { Icon(ImageVector.vectorResource(R.drawable.support), null) },
        content = R.string.support_developer,
        supportingContent = R.string.support_developer_desc,
        trailingContent = { Icon(Icons.Rounded.OpenInNew, null) }
    )
)