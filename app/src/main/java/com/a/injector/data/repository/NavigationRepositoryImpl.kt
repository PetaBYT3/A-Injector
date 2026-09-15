package com.a.injector.data.repository

import com.a.injector.domain.model.state.NavigationState
import com.a.injector.domain.repository.NavigationRepository
import com.a.injector.presentation.navigation.NavigationRoute
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import org.koin.core.annotation.Single

@Single
class NavigationRepositoryImpl: NavigationRepository {
    private val _navigationEffect = Channel<NavigationState>()
    override val navigationEffect: Flow<NavigationState> = _navigationEffect.receiveAsFlow()

    override suspend fun navigateTo(navigationRoute: NavigationRoute) {
        _navigationEffect.send(NavigationState.NavigateTo(navigationRoute))
    }

    override suspend fun replaceTo(navigationRoute: NavigationRoute) {
        _navigationEffect.send(NavigationState.ReplaceTo(navigationRoute))
    }

    override suspend fun popBackStack() {
        _navigationEffect.send(NavigationState.PopBackStack)
    }
}