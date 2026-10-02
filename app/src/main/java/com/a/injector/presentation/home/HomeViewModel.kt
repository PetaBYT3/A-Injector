package com.a.injector.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.a.injector.BuildConfig
import com.a.injector.domain.repository.ApplicationRepository
import com.a.injector.domain.repository.InjectRepository
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
class HomeViewModel(
    private val applicationRepository: ApplicationRepository,
    private val injectRepository: InjectRepository,
    private val profileRepository: ProfileRepository
): ViewModel() {
    private val _state = MutableStateFlow(HomeState())
    val state = _state.asStateFlow()

    private val _effect = Channel<ScreenEffect>()
    val effect = _effect.receiveAsFlow()

    init {
        viewModelScope.launch {
            applicationRepository.getVersion().collect { either ->
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

        viewModelScope.launch {
            injectRepository.currentInjectMethod.collect { injectModel ->
                _state.update { currentState ->
                    currentState.copy(
                        inject = injectModel,
                        isInjectLoading = false
                    )
                }
            }
        }

        viewModelScope.launch {
            profileRepository.getListBySupport().collect { either ->
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
            profileRepository.getListByContribution().collect { either ->
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
            HomeAction.MaintenanceBottomSheet -> {
                _state.update { currentState ->
                    currentState.copy(isMaintenanceBottomSheetVisible = !currentState.isMaintenanceBottomSheetVisible)
                }
            }
            HomeAction.UpdateBottomSheet -> {
                _state.update { currentState ->
                    currentState.copy(isUpdateBottomSheetVisible = !currentState.isUpdateBottomSheetVisible)
                }
            }
            is HomeAction.ShowSnackBar -> {
                viewModelScope.launch {
                    _effect.send(ScreenEffect.ShowSnackBar(action.text))
                }
            }
        }
    }
}