package com.a.injector.data.repository

import com.a.injector.domain.model.state.NavigationState
import com.a.injector.domain.repository.NavigationRepository
import com.a.injector.presentation.mainnavigation.MainNavigationRoute
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import org.koin.core.annotation.Single

@Single
class NavigationRepositoryImpl: NavigationRepository {
    private val _navigationEffect = Channel<NavigationState>(Channel.BUFFERED)
    override val navigationEffect: Flow<NavigationState> = _navigationEffect.receiveAsFlow()

    override suspend fun navigateTo(mainNavigationRoute: MainNavigationRoute) {
        _navigationEffect.send(NavigationState.NavigateTo(mainNavigationRoute))
    }

    override suspend fun replaceTo(mainNavigationRoute: MainNavigationRoute) {
        _navigationEffect.send(NavigationState.ReplaceTo(mainNavigationRoute))
    }

    override suspend fun popBackStack() {
        _navigationEffect.send(NavigationState.PopBackStack)
    }
}