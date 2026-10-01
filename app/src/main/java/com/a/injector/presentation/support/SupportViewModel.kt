package com.a.injector.presentation.support

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.a.injector.domain.repository.NavigationRepository
import com.a.injector.domain.repository.ProfileRepository
import com.a.injector.domain.repository.SupportRepository
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
class SupportViewModel(
    private val profileRepository: ProfileRepository,
    private val supportRepository: SupportRepository,
    private val navigationRepository: NavigationRepository
): ViewModel() {
    private val _state = MutableStateFlow(SupportState())
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
                            isProfileLoading = false
                        )
                    }
                }.onLeft { textResource ->
                    _state.update { currentState ->
                        currentState.copy(
                            isProfileError = textResource,
                            isProfileLoading = false
                        )
                    }
                }
            }
        }

        viewModelScope.launch {
            supportRepository.getCurrent().collect { either ->
                either.onRight { supportModel ->
                    _state.update { currentState ->
                        currentState.copy(
                            requestedSupport = supportModel,
                            isRequestedSupportLoading = false
                        )
                    }
                }.onLeft { textResource ->
                    _state.update { currentState ->
                        currentState.copy(
                            isRequestedSupportError = textResource,
                            isRequestedSupportLoading = false
                        )
                    }
                }
            }
        }
    }

    fun onAction(action: SupportAction) {
        when (action) {
            is SupportAction.NominalTextField -> {
                _state.update { currentState ->
                    currentState.copy(
                        requestedSupport = currentState.requestedSupport.copy(
                            support = action.nominal.ifBlank { "0" }.toLong()
                        )
                    )
                }
            }
            is SupportAction.ImagePicker -> {
                _state.update { currentState ->
                    currentState.copy(imageToUpload = action.image)
                }
            }
            SupportAction.UpsertSupportingButton -> {
                upsertSupportingButton()
            }
        }
    }

    private fun upsertSupportingButton() {
        viewModelScope.launch {
            supportRepository.upsert(
                supportModel = _state.value.requestedSupport.copy(
                    id = _state.value.profile.id
                ),
                image = _state.value.imageToUpload
            ).onStart {
                _state.update { currentState ->
                    currentState.copy(isUpsertSupportButtonLoading = true)
                }
            }.onCompletion {
                _state.update { currentState ->
                    currentState.copy(isUpsertSupportButtonLoading = false)
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