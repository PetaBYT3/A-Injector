package com.a.injector.presentation.profile

sealed interface ProfileAction {
    data object RequestContributorButton: ProfileAction

    data object SignOutBottomSheet: ProfileAction
    data object SignOutButton: ProfileAction
}