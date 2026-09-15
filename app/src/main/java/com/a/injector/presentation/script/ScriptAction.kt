package com.a.injector.presentation.script

sealed interface ScriptAction {
    data class SearchTextField(val keyword: String): ScriptAction
}