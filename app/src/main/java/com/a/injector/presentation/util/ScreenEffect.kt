package com.a.injector.presentation.util

import com.a.injector.data.util.TextResource

sealed interface ScreenEffect {
    data class ShowSnackBar(val message: TextResource): ScreenEffect
    data class ShowToast(val message: TextResource): ScreenEffect
}