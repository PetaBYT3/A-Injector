package com.a.injector.presentation.home

import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.a.injector.R
import com.a.injector.domain.model.StaticModel

fun aboutDeveloper(
    navBackStack: NavBackStack<NavKey>,
    onAction: (HomeAction) -> Unit
): List<StaticModel> {
    return listOf(
        StaticModel(
            contentTextResId = R.string.github
        )
    )
}