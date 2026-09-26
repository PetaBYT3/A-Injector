package com.a.injector.domain.model.state

import com.a.injector.presentation.mainnavigation.MainNavigationRoute

sealed interface NavigationState {
    data class NavigateTo(val route: MainNavigationRoute): NavigationState
    data class ReplaceTo(val route: MainNavigationRoute): NavigationState
    data object PopBackStack: NavigationState
}