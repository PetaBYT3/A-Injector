package com.a.injector.presentation.signup

data class SignUpState(
    val emailTextField: String = "",
    val passwordTextField: String = "",
    val passwordHasEightChar: Boolean = false,
    val passwordHasUppercase: Boolean = false,
    val passwordHasNumber: Boolean = false,

    val isSignUpButtonLoading: Boolean = false
) {
    val isDataValid: Boolean get() =
        emailTextField.isNotBlank() &&
        passwordTextField.isNotBlank() &&
        passwordHasEightChar &&
        passwordHasUppercase &&
        passwordHasNumber
}
