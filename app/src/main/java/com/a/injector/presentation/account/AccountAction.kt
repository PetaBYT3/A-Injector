package com.a.injector.presentation.account

sealed interface AccountAction {
    data object TerminateSessionBottomSheet: AccountAction
    data object TerminateSessionButton: AccountAction

    data object SignOutBottomSheet: AccountAction
    data object SignOutButton: AccountAction
}