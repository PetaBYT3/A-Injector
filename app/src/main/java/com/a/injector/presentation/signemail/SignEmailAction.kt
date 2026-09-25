package com.a.injector.presentation.signemail

sealed interface SignEmailAction {
    data class EmailTextField(val email: String): SignEmailAction
    data object SendOtpButton: SignEmailAction

    data class OtpTextField(val otp: String): SignEmailAction
    data object VerifyOtpButton: SignEmailAction
}