package com.a.injector.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.a.injector.domain.model.state.CommandService
import com.a.injector.domain.repository.AccountRepository
import com.a.injector.domain.repository.InjectRepository
import com.a.injector.domain.repository.PermissionRepository
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
    private val accountRepository: AccountRepository
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
            accountRepository.getProfileByHighestContribution().collect { either ->
                either.onRight { profileModels ->
                    _state.update { currentState ->
                        currentState.copy(
                            highestContributionProfile = profileModels,
                            isHighestContributionProfileLoading = false
                        )
                    }
                }.onLeft { error ->
                    _state.update { currentState ->
                        currentState.copy(
                            isHighestContributionProfileError = error,
                            isHighestContributionProfileLoading = false
                        )
                    }
                }
            }
        }
    }

    fun onAction(action: HomeAction) {
        when (action) {
            is HomeAction.SetCommandServiceButton -> {
                setCommandServiceButton(action.commandService)
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