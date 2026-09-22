package com.a.injector.presentation.bottomnavigation

import com.a.injector.R
import com.a.injector.domain.model.StaticModel

enum class BottomNavigationId {
    Home, Script, Account, Settings
}

val bottomNavigationItems = listOf(
    StaticModel(
        id = BottomNavigationId.Home,
        contentTextResId = R.string.title_home
    ),
    StaticModel(
        id = BottomNavigationId.Script,
        contentTextResId = R.string.title_script
    ),
    StaticModel(
        id = BottomNavigationId.Account,
        contentTextResId = R.string.title_account
    ),
    StaticModel(
        id = BottomNavigationId.Settings,
        contentTextResId = R.string.title_settings
    )
)