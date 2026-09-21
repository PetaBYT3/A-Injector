package com.a.injector.presentation.loading

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.a.injector.BuildConfig
import com.a.injector.domain.model.state.AuthResult
import com.a.injector.domain.repository.AccountRepository
import com.a.injector.domain.repository.DatabaseRepository
import com.a.injector.domain.repository.NavigationRepository
import com.a.injector.presentation.navigation.NavigationRoute
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class LoadingViewModel(
    private val accountRepository: AccountRepository,
    private val databaseRepository: DatabaseRepository,
    private val navigationRepository: NavigationRepository
): ViewModel() {
    private val _state = MutableStateFlow(LoadingState())
    val state = _state.asStateFlow()

    private val isAppValidationComplete = MutableStateFlow(false)

    init {
        viewModelScope.launch {
            databaseRepository.getVersion().collect { either ->
                either.onRight { versionModel ->
                    Log.d("LoadingDebug", versionModel.toString())
                    if (versionModel.maintenance) {
                        _state.update { currentState ->
                            currentState.copy(isMaintenanceBottomSheetVisible = true)
                        }
                        return@onRight
                    }

                    if (versionModel.version != BuildConfig.VERSION_CODE) {
                        _state.update { currentState ->
                            currentState.copy(isUpdateBottomSheetVisible = true)
                        }
                        return@onRight
                    }

                    isAppValidationComplete.update { true }
                }.onLeft { error ->
                    Log.e("LoadingDebug", error)
                }
            }
        }

        viewModelScope.launch {
            isAppValidationComplete.first { it }
            accountRepository.authState.filterNotNull().collect { currentAuth ->
                when (currentAuth) {
                    AuthResult.Unauthenticated -> {
                        navigationRepository.replaceTo(NavigationRoute.LandingScreen)
                    }
                    AuthResult.Authenticated -> {
                        navigationRepository.replaceTo(NavigationRoute.BottomNavigation)
                    }
                }
            }
        }
    }
}