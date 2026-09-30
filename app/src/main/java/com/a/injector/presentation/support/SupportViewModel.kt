package com.a.injector.presentation.support

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.a.injector.domain.model.ProfileModel
import com.a.injector.domain.model.SupportModel
import com.a.injector.domain.repository.NavigationRepository
import com.a.injector.domain.repository.UserRepository
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
import org.koin.core.annotation.InjectedParam

@KoinViewModel
class SupportViewModel(
    @InjectedParam private val profileId: String,
    private val userRepository: UserRepository,
    private val navigationRepository: NavigationRepository
): ViewModel() {
    private val _state = MutableStateFlow(SupportState())
    val state = _state.asStateFlow()

    private val _effect = Channel<ScreenEffect>()
    val effect = _effect.receiveAsFlow()

    fun onAction(action: SupportAction) {
        when (action) {
            is SupportAction.NominalTextField -> {
                _state.update { currentState ->
                    currentState.copy(nominalTextField = action.nominal)
                }
            }
            is SupportAction.ImagePicker -> {
                _state.update { currentState ->
                    currentState.copy(image = action.image)
                }
            }
            SupportAction.UpsertSupportingButton -> {
                upsertSupportingButton()
            }
        }
    }

    private fun upsertSupportingButton() {
        viewModelScope.launch {

            userRepository.upsertSupporting(
                supportModel = SupportModel(
                    id = profileId,
                    nominal = _state.value.nominalTextField.ifBlank { "0" }.toLong(),
                    imageUrl = "",
                    profile = ProfileModel.EMPTY
                ),
                image = _state.value.image
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