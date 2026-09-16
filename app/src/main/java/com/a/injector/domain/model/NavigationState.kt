package com.a.injector.domain.model.state

import com.a.injector.presentation.navigation.NavigationRoute

sealed interface NavigationState {
    data class NavigateTo(val route: NavigationRoute): NavigationState
    data class ReplaceTo(val route: NavigationRoute): NavigationState
    data object PopBackStack: NavigationState
}