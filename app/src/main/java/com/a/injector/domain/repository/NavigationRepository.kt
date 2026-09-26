package com.a.injector.domain.repository

import com.a.injector.domain.model.state.NavigationState
import com.a.injector.presentation.mainnavigation.MainNavigationRoute
import kotlinx.coroutines.flow.Flow

interface NavigationRepository {
    val navigationEffect: Flow<NavigationState>

    suspend fun navigateTo(mainNavigationRoute: MainNavigationRoute)
    suspend fun replaceTo(mainNavigationRoute: MainNavigationRoute)
    suspend fun popBackStack()
}