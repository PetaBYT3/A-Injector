package com.a.injector.presentation.resetpassword

sealed interface ResetPasswordAction {
    data class EmailTextField(val email: String): ResetPasswordAction
    data object SendResetButton: ResetPasswordAction
}