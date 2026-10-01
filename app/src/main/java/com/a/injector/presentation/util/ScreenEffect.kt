package com.a.injector.presentation.util

import com.a.injector.domain.model.Text

sealed interface ScreenEffect {
    data class ShowSnackBar(val message: Text): ScreenEffect
    data class ShowToast(val message: Text): ScreenEffect
}