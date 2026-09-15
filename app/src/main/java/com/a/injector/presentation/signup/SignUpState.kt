package com.a.injector.presentation.signup

data class SignUpState(
    val emailTextField: String = "",
    val passwordTextField: String = "",

    val isSignUpButtonLoading: Boolean = false
) {
    val isPasswordMoreThan8Character: Boolean get() = passwordTextField.length >= 8
    val isPasswordContainUppercase: Boolean get() = passwordTextField.any { it.isUpperCase() }
    val isPasswordContainNumber: Boolean get() = passwordTextField.any { it.isDigit() }

    val isDataValid: Boolean get() =
        emailTextField.isNotEmpty() &&
        passwordTextField.isNotEmpty() &&
        isPasswordMoreThan8Character &&
        isPasswordContainUppercase &&
        isPasswordContainNumber
}
