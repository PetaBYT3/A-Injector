package com.a.injector.presentation.signlink

sealed interface SignLinkAction {
    data class EmailTextField(val email: String): SignLinkAction
    data object SendOtpButton: SignLinkAction
}