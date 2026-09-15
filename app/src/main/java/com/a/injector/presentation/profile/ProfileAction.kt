package com.a.injector.presentation.profile

sealed interface ProfileAction {
    data object RequestContributorButton: ProfileAction
}