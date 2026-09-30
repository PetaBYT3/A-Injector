package com.a.injector.presentation.bottomnavigation

import com.a.injector.R
import com.a.injector.domain.model.StaticModel

enum class BottomNavigationId {
    Home, Script, Account, Settings
}

val bottomNavigationItems = listOf(
    StaticModel(
        id = BottomNavigationId.Home,
        content = R.string.title_home
    ),
    StaticModel(
        id = BottomNavigationId.Script,
        content = R.string.title_script
    ),
    StaticModel(
        id = BottomNavigationId.Account,
        content = R.string.title_account
    ),
    StaticModel(
        id = BottomNavigationId.Settings,
        content = R.string.title_settings
    )
)