package com.a.injector.domain.repository

import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.a.injector.domain.model.StaticMenu
import com.a.injector.presentation.profile.ProfileAction

object StaticRepository {
    fun getProfileMenu(
        navBackStack: NavBackStack<NavKey>,
        onAction: (ProfileAction) -> Unit
    ): List<StaticMenu> {
        return listOf(
            StaticMenu(
                overlineTextResId = TODO(),
                titleResId = TODO(),
                supportingTextResId = TODO()
            )
        )
    }
}