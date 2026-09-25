package com.a.injector.presentation.signemail

data class SignEmailState(
    val isVerifyingOtp: Boolean = false,

    val emailTextField: String = "",
    val isSendOtpButtonLoading: Boolean = false,

    val otpTextField: String = "",
    val isVerifyOtpButtonLoading: Boolean = false
)
