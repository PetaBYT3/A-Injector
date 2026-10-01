package com.a.injector.presentation.password

data class PasswordState(
    val passwordTextField: String = "",
    val passwordHasEightChar: Boolean = false,
    val passwordHasUppercase: Boolean = false,
    val passwordHasNumber: Boolean = false,
    val passwordValid: Boolean = false,

    val isChangePasswordButtonLoading: Boolean = false,
)
