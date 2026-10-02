package com.a.injector.presentation.debug

sealed interface DebugAction {
    data object Start: DebugAction
    data object Run: DebugAction
    data class TextField(val text: String): DebugAction
}