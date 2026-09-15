package com.a.injector.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.a.injector.data.dto.Role
import com.a.injector.domain.model.RequestModel
import com.a.injector.domain.model.state.AuthState
import com.a.injector.domain.repository.AccountRepository
import com.a.injector.presentation.util.ScreenEffect
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel
import kotlin.uuid.Uuid

@KoinViewModel
class ProfileViewModel(
    private val accountRepository: AccountRepository
): ViewModel() {
    private val _state = MutableStateFlow(ProfileState())
    val state = _state.asStateFlow()

    private val _effect = Channel<ScreenEffect>()
    val effect = _effect.receiveAsFlow()

    init {
        viewModelScope.launch {
            accountRepository.currentAuth.collect { authState ->
                if (authState is AuthState.Authorized) {
                    _state.update { currentState ->
                        currentState.copy(
                            userInfo = authState.userInfo,
                            profile = authState.profileModel,
                            isProfileLoading = false
                        )
                    }
                }
            }
        }
    }

    fun onAction(action: ProfileAction) {
        when (action) {
            ProfileAction.RequestContributorButton -> {
                requestContributorButton()
            }
        }
    }

    private fun requestContributorButton() {
        viewModelScope.launch {
            accountRepository.upsertRequest(
                requestModel = RequestModel(
                    id = Uuid.random().toString(),
                    profileId = "",
                    role = Role.Contributor
                )
            ).collect { either ->
                either.onRight {
                    _effect.send(ScreenEffect.ShowSnackBar("Request Sent"))
                }.onLeft { error ->
                    _effect.send(ScreenEffect.ShowSnackBar(error))
                }
            }
        }
    }
}
