package com.a.injector.presentation.util

sealed interface ScreenEffect {
    data class ShowSnackBar(val message: String): ScreenEffect
}