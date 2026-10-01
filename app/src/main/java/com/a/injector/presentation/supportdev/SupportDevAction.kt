package com.a.injector.presentation.supportdev

sealed interface SupportDevAction {
    data object QrisBottomSheet: SupportDevAction
}