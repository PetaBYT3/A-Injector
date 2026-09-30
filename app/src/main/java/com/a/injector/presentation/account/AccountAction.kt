package com.a.injector.presentation.account

sealed interface AccountAction {
    data object SignOutBottomSheet: AccountAction
    data object SignOutButton: AccountAction
}