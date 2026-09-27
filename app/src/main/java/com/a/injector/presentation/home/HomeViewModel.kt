package com.a.injector.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.a.injector.domain.model.state.CommandService
import com.a.injector.domain.repository.InjectRepository
import com.a.injector.domain.repository.PermissionRepository
import com.a.injector.domain.repository.UserRepository
import com.a.injector.presentation.util.ScreenEffect
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class HomeViewModel(
    private val permissionRepository: PermissionRepository,
    private val injectRepository: InjectRepository,
    private val userRepository: UserRepository
): ViewModel() {
    private val _state = MutableStateFlow(HomeState())
    val state = _state.asStateFlow()

    private val _effect = Channel<ScreenEffect>()
    val effect = _effect.receiveAsFlow()

    init {
        viewModelScope.launch {
            permissionRepository.isManageExternalStorageGranted.collect { isGranted ->
                _state.update { currentState ->
                    currentState.copy(isManageExternalStorageGranted = isGranted)
                }
            }
        }

        viewModelScope.launch {
            userRepository.getTopSupporter().collect { either ->
                either.onRight { profileModels ->
                    _state.update { currentState ->
                        currentState.copy(
                            topSupporter = profileModels,
                            isTopSupporterLoading = false
                        )
                    }
                }.onLeft { error ->
                    _state.update { currentState ->
                        currentState.copy(
                            isTopSupporterError = error,
                            isTopSupporterLoading = false
                        )
                    }
                }
            }
        }

        viewModelScope.launch {
            userRepository.getTopContributor().collect { either ->
                either.onRight { profileModels ->
                    _state.update { currentState ->
                        currentState.copy(
                            topContribution = profileModels,
                            isTopContributionLoading = false
                        )
                    }
                }.onLeft { error ->
                    _state.update { currentState ->
                        currentState.copy(
                            isTopContributionError = error,
                            isTopContributionLoading = false
                        )
                    }
                }
            }
        }
    }

    fun onAction(action: HomeAction) {
        when (action) {
            is HomeAction.SetCommandServiceButton -> {
                setCommandServiceButton(commandService = action.commandService)
            }
            HomeAction.SupportBottomSheet -> {
                _state.update { currentState ->
                    currentState.copy(isSupportBottomSheetVisible = !currentState.isSupportBottomSheetVisible)
                }
            }
        }
    }

    private fun setCommandServiceButton(commandService: CommandService) {
        viewModelScope.launch {
            injectRepository.setCommandService(
                commandService = commandService
            ).collect { either ->
                either.onLeft { error ->
                    _effect.send(ScreenEffect.ShowSnackBar(error))
                }
            }
        }
    }
}