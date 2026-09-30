package com.a.injector.presentation.paneluser

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.a.injector.domain.repository.ProfileRepository
import com.a.injector.presentation.util.ScreenEffect
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class PanelUserViewModel(
    private val profileRepository: ProfileRepository
): ViewModel() {
    private val _state = MutableStateFlow(PanelUserState())
    val state = _state.asStateFlow()

    private val _effect = Channel<ScreenEffect>()
    val effect = _effect.receiveAsFlow()

    init {
        viewModelScope.launch {
            profileRepository.getList().collect { either ->
                either.onRight { profileModels ->
                    _state.update { currentState ->
                        currentState.copy(profiles = profileModels, isProfileLoading = false)
                    }
                    searchTextField("")
                }.onLeft { error ->
                    _state.update { currentState ->
                        currentState.copy(isProfileError = error, isProfileLoading = false)
                    }
                }
            }
        }
    }

    fun onAction(action: PanelUserAction) {
        when (action) {
            is PanelUserAction.SearchTextField -> {
                searchTextField(keyword = action.keyword)
            }
        }
    }

    private fun searchTextField(keyword: String) {
        val filteredProfile = if (keyword.isBlank()) {
            _state.value.profiles
        } else {
            _state.value.profiles.filter { profileModel ->
                profileModel.username.contains(keyword, true)
            }
        }
        _state.update { currentState ->
            currentState.copy(searchTextField = keyword, filteredProfiles = filteredProfile)
        }
    }
}