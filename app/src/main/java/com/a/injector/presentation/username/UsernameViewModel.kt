package com.a.injector.presentation.username

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.a.injector.domain.repository.NavigationRepository
import com.a.injector.domain.repository.ProfileRepository
import com.a.injector.presentation.util.ScreenEffect
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class UsernameViewModel(
    private val profileRepository: ProfileRepository,
    private val navigationRepository: NavigationRepository
): ViewModel() {
    private val _state = MutableStateFlow(UsernameState())
    val state = _state.asStateFlow()

    private val _effect = Channel<ScreenEffect>()
    val effect = _effect.receiveAsFlow()

    init {
        viewModelScope.launch {
            profileRepository.getCurrent().collect { either ->
                either.onRight { profileModel ->
                    _state.update { currentState ->
                        currentState.copy(
                            profile = profileModel,
                            isProfileLoadingLoading = false
                        )
                    }
                }.onLeft { textResource ->
                    _state.update { currentState ->
                        currentState.copy(
                            isProfileError = textResource,
                            isProfileLoadingLoading = false
                        )
                    }
                }
            }
        }
    }

    fun onAction(action: UsernameAction) {
        when (action) {
            is UsernameAction.UsernameTextField -> {
                _state.update { currentState ->
                    currentState.copy(
                        profile = currentState.profile.copy(username = action.username)
                    )
                }
            }
            UsernameAction.UpsertProfileButton -> {
                upsertProfileButton()
            }
        }
    }

    private fun upsertProfileButton() {
        viewModelScope.launch {
            profileRepository.upsert(
                profileModel = _state.value.profile
            ).onStart {
                _state.update { currentState ->
                    currentState.copy(isUpsertProfileButtonLoading = true)
                }
            }.onCompletion {
                _state.update { currentState ->
                    currentState.copy(isUpsertProfileButtonLoading = false)
                }
            }.collect { either ->
                either.onRight {
                    navigationRepository.popBackStack()
                }.onLeft { error ->
                    _effect.send(ScreenEffect.ShowSnackBar(error))
                }
            }
        }
    }
}