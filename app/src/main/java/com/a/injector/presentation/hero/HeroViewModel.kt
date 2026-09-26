package com.a.injector.presentation.hero

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.a.injector.R
import com.a.injector.data.dto.Role
import com.a.injector.data.util.TextResource
import com.a.injector.domain.model.ReplaceModel
import com.a.injector.domain.model.SkinModel
import com.a.injector.domain.repository.AccountRepository
import com.a.injector.domain.repository.InjectRepository
import com.a.injector.domain.repository.ScriptRepository
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
class HeroViewModel(
    @InjectedParam private val heroId: String,
    private val accountRepository: AccountRepository,
    private val scriptRepository: ScriptRepository,
    private val injectRepository: InjectRepository
): ViewModel() {
    private val _state = MutableStateFlow(HeroState())
    val state = _state.asStateFlow()

    private val _effect = Channel<ScreenEffect>()
    val effect = _effect.receiveAsFlow()

    init {
        viewModelScope.launch {
            accountRepository.currentProfile.collect { profileModel ->
                _state.update { currentState ->
                    currentState.copy(
                        isModifyEnabled = profileModel.role != Role.User
                    )
                }
            }
        }

        viewModelScope.launch {
            scriptRepository.getHero(
                heroId = heroId
            ).collect { either ->
                either.onRight { heroDetailModel ->
                    _state.update { currentState ->
                        currentState.copy(
                            heroDetail = heroDetailModel,
                            isHeroDetailLoading = false
                        )
                    }
                }.onLeft { error ->
                    _state.update { currentState ->
                        currentState.copy(
                            isHeroDetailError = error,
                            isHeroDetailLoading = false
                        )
                    }
                }
            }
        }
    }

    fun onAction(action: HeroAction) {
        when (action) {
            is HeroAction.ShowActionSkinBottomSheet -> {
                _state.update { currentState ->
                    currentState.copy(
                        isActionSkinBottomSheetVisible = true,
                        skinToAction = action.skin
                    )
                }
            }
            HeroAction.DismissSkinActionBottomSheet -> {
                _state.update { currentState ->
                    currentState.copy(isActionSkinBottomSheetVisible = false)
                }
            }
            is HeroAction.StartInject -> {
                startInject(skinModel = action.skin, replaceModel = action.replace)
            }
        }
    }

    private fun startInject(skinModel: SkinModel, replaceModel: ReplaceModel) {
        viewModelScope.launch {
            injectRepository.execute(
                replaceModel = replaceModel
            ).onStart {
                _state.update { currentState ->
                    currentState.copy(
                        targetSkin = skinModel,
                        targetReplace = replaceModel,
                        injectStatus = TextResource.DynamicString("Starting"),
                        isInjectBottomSheetVisible = true
                    )
                }
            }.onCompletion {
                _state.update { currentState ->
                    currentState.copy(
                        isInjectBottomSheetVisible = false
                    )
                }
                _effect.send(
                    element = ScreenEffect.ShowSnackBar(
                        message = TextResource.StringResource(R.string.success_install_script)
                    )
                )
            }.collect { either ->
                either.onRight { message ->
                    _state.update { currentState ->
                        currentState.copy(injectStatus = message)
                    }
                }.onLeft { error ->
                    _effect.send(ScreenEffect.ShowSnackBar(error))
                }
            }
        }
    }
}