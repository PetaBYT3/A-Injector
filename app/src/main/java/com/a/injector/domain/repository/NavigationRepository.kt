package com.a.injector.domain.repository

import com.a.injector.domain.model.state.NavigationState
import com.a.injector.presentation.navigation.NavigationRoute
import kotlinx.coroutines.flow.Flow

interface NavigationRepository {
    val navigationEffect: Flow<NavigationState>

    suspend fun navigateTo(navigationRoute: NavigationRoute)
    suspend fun replaceTo(navigationRoute: NavigationRoute)
    suspend fun popBackStack()
}