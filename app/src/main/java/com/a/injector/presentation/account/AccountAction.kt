package com.a.injector.presentation.account

sealed interface AccountAction {
    data object RequestContributorButton: AccountAction

    data object SignOutBottomSheet: AccountAction
    data object SignOutButton: AccountAction
}