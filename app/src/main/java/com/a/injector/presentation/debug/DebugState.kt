package com.a.injector.presentation.debug

data class DebugState(
    val isStartButtonLoading: Boolean = false,
    val textField: String = "",
    val output: String = ""
)
