package com.a.injector.presentation.signup

sealed interface SignUpAction {
    data class EmailTextField(val email: String): SignUpAction
    data class PasswordTextField(val password: String): SignUpAction
    data object SignUpButton: SignUpAction
}