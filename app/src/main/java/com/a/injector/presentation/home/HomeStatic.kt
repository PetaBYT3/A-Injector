package com.a.injector.presentation.home

import com.a.injector.R
import com.a.injector.domain.model.StaticModel

enum class HomeAboutDeveloperId {
    Github, Support
}

val homeAboutDeveloperItems = listOf(
    StaticModel(
        id = HomeAboutDeveloperId.Github,
        contentTextResId = R.string.item_github
    ),
    StaticModel(
        id = HomeAboutDeveloperId.Support,
        contentTextResId = R.string.item_support
    )
)