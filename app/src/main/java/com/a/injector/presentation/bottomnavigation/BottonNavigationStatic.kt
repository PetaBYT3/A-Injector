package com.a.injector.presentation.bottomnavigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.InsertDriveFile
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.Icon
import com.a.injector.R
import com.a.injector.domain.model.StaticModel

enum class BottomNavigationId {
    Home, Script, Account, Settings
}

val bottomNavigationItems = listOf(
    StaticModel(
        id = BottomNavigationId.Home,
        leadingContent = { Icon(Icons.Rounded.Home, null) },
        content = R.string.home
    ),
    StaticModel(
        id = BottomNavigationId.Script,
        leadingContent = { Icon(Icons.Rounded.InsertDriveFile, null) },
        content = R.string.script
    ),
    StaticModel(
        id = BottomNavigationId.Account,
        leadingContent = { Icon(Icons.Rounded.Person, null) },
        content = R.string.account
    ),
    StaticModel(
        id = BottomNavigationId.Settings,
        leadingContent = { Icon(Icons.Rounded.Settings, null) },
        content = R.string.settings
    )
)