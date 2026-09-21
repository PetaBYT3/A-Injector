package com.a.injector.presentation.resetpassword

data class ResetPasswordState(
    val emailTextField: String = "",
    val isSendResetButtonLoading: Boolean = false
)
