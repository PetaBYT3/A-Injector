package com.a.injector.presentation.bottomnavigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.a.injector.BuildConfig
import com.a.injector.presentation.util.ScreenEffect
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class BottomNavigationViewModel(
    private val databaseRepository: DatabaseRepository
): ViewModel() {
    private val _state = MutableStateFlow(BottomNavigationState())
    val state = _state.asStateFlow()

    private val _effect = Channel<ScreenEffect>()
    val effect = _effect.receiveAsFlow()

    init {
        viewModelScope.launch {
            databaseRepository.getVersion().collect { either ->
                either.onRight { versionModel ->
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
                }.onLeft { error ->
                    _effect.send(ScreenEffect.ShowToast(error))
                }
            }
        }
    }

    fun onAction(action: BottomNavigationAction) {
        when (action) {
            BottomNavigationAction.MaintenanceBottomSheet -> {
                _state.update { currentState ->
                    currentState.copy(isMaintenanceBottomSheetVisible = !currentState.isMaintenanceBottomSheetVisible)
                }
            }
            BottomNavigationAction.UpdateBottomSheet -> {
                _state.update { currentState ->
                    currentState.copy(isUpdateBottomSheetVisible = !currentState.isUpdateBottomSheetVisible)
                }
            }
        }
    }
}