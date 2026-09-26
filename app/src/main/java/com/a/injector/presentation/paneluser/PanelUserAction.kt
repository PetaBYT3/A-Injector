package com.a.injector.presentation.paneluser

sealed interface PanelUserAction {
    data class SearchTextField(val keyword: String): PanelUserAction
}