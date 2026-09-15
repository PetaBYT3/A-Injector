package com.a.injector.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.a.injector.data.local.UserDataStoreApi
import com.a.injector.data.dto.Executor
import com.a.injector.domain.repository.SuperuserRepository
import com.a.injector.domain.repository.ShizukuRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class HomeViewModel(
    private val userDataStoreApi: UserDataStoreApi,
    private val shizukuRepository: ShizukuRepository,
    private val superuserRepository: SuperuserRepository
): ViewModel() {

    private val _state = MutableStateFlow(HomeState())
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            userDataStoreApi.executor.collect { executor ->
                _state.update { it.copy(executor = executor) }
            }
        }

        viewModelScope.launch {
            shizukuRepository.isAuthorized.collect { isAuthorized ->
                _state.update { it.copy(isAuthorized = isAuthorized) }
            }
        }

        viewModelScope.launch {
            superuserRepository.isGranted.collect { isGranted ->
                _state.update { it.copy(isGranted = isGranted) }
            }
        }
    }

    fun onAction(action: HomeAction) {
        when (action) {
            is HomeAction.SetExecutor -> {
                setExecutor(executor = action.executor)
            }
        }
    }

    private fun setExecutor(executor: Executor) {
        viewModelScope.launch {
            userDataStoreApi.setExecutor(executor = executor)
        }
    }
}