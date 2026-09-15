package com.a.injector.presentation.signin

data class SignInState(
    val emailTextField: String = "",
    val passwordTextField: String = "",

    val isSingInButtonLoading: Boolean = false
) {
    val isDataValid: Boolean get() =
        emailTextField.isNotBlank() &&
        passwordTextField.isNotBlank()
}
