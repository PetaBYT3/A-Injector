package com.a.injector.presentation.password

sealed interface PasswordAction {
    data class PasswordTextField(val password: String): PasswordAction
    data object ChangePasswordButton: PasswordAction
}