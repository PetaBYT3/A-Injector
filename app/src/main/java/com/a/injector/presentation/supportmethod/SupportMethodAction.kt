package com.a.injector.presentation.supportmethod

sealed interface SupportMethodAction {
    data object QrisBottomSheet: SupportMethodAction
    data class DownloadDrawable(val drawable: Int): SupportMethodAction
}