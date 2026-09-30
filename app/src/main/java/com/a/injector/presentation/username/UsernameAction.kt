package com.a.injector.presentation.username

sealed interface UsernameAction {
    data class UsernameTextField(val username: String): UsernameAction
    data object UpsertProfileButton: UsernameAction
}