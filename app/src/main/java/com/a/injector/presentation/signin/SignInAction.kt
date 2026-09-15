package com.a.injector.presentation.signin

sealed interface SignInAction {
    data class EmailTextField(val email: String): SignInAction
    data class PasswordTextField(val password: String): SignInAction

    data object SignInButton: SignInAction
}