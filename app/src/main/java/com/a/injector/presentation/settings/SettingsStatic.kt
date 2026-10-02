package com.a.injector.presentation.settings

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AdminPanelSettings
import androidx.compose.material.icons.rounded.CleaningServices
import androidx.compose.material.icons.rounded.Language
import androidx.compose.material.icons.rounded.PersonAdd
import androidx.compose.material3.Icon
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import com.a.injector.R
import com.a.injector.domain.model.StaticModel
import java.util.Locale

enum class CloudSetting {
    PanelSupporting, RoleManager, UserPanel, CleanStorage
}

val cloudSettings = listOf(
    StaticModel(
        id = CloudSetting.PanelSupporting,
        leadingContent = { Icon(ImageVector.vectorResource(R.drawable.support), null) },
        content = R.string.support_panel,
        supportingContent = R.string.support_panel_desc
    ),
    StaticModel(
        id = CloudSetting.RoleManager,
        leadingContent = { Icon(Icons.Rounded.AdminPanelSettings, null) },
        content = R.string.role_panel,
        supportingContent = R.string.role_panel_desc
    ),
    StaticModel(
        id = CloudSetting.UserPanel,
        leadingContent = { Icon(Icons.Rounded.PersonAdd, null) },
        content = R.string.user_panel,
        supportingContent = R.string.user_panel_desc
    ),
    StaticModel(
        id = CloudSetting.CleanStorage,
        leadingContent = { Icon(Icons.Rounded.CleaningServices, null) },
        content = R.string.clean_cloud_storage,
        supportingContent = R.string.clean_cloud_storage_desc
    ),
)

val supportedLanguage = listOf(
    Locale.US, Locale("id", "ID")
)

enum class DeviceSetting {
    Inject, Language, CleanCache
}

val deviceSettings = listOf(
    StaticModel(
        id = DeviceSetting.Inject,
        leadingContent = { Icon(ImageVector.vectorResource(R.drawable.inject), null) },
        content = R.string.inject_method
    ),
    StaticModel(
        id = DeviceSetting.Language,
        leadingContent = { Icon(Icons.Rounded.Language, null) },
        content = R.string.language
    ),
    StaticModel(
        id = DeviceSetting.CleanCache,
        leadingContent = { Icon(Icons.Rounded.CleaningServices, null) },
        content = R.string.clean_cache
    )
)

enum class AboutApp {
    Kotlin, JetpackCompose, Supabase
}

val aboutApps = listOf(
    StaticModel(
        id = AboutApp.Kotlin,
        content = R.string.kotlin,
        leadingContent = { Icon(ImageVector.vectorResource(R.drawable.kotlin), null) },
        supportingContent = R.string.kotlin_desc
    ),
    StaticModel(
        id = AboutApp.JetpackCompose,
        leadingContent = { Icon(ImageVector.vectorResource(R.drawable.compose), null) },
        content = R.string.jetpack_compose,
        supportingContent = R.string.jetpack_compose_desc
    ),
    StaticModel(
        id = AboutApp.Supabase,
        leadingContent = { Icon(ImageVector.vectorResource(R.drawable.supabase), null) },
        content = R.string.supabase,
        supportingContent = R.string.supabase_desc
    )
)