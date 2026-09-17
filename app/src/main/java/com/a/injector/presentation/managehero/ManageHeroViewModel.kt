package com.a.injector.presentation.managehero

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.a.injector.domain.repository.DatabaseRepository
import com.a.injector.domain.repository.NavigationRepository
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
import kotlin.uuid.Uuid

@KoinViewModel
class ManageHeroViewModel(
    @InjectedParam private val heroId: String,
    private val databaseRepository: DatabaseRepository,
    private val navigationRepository: NavigationRepository
): ViewModel() {
    private val _state = MutableStateFlow(ManageHeroState())
    val state = _state.asStateFlow()

    private val _effect = Channel<ScreenEffect>()
    val effect = _effect.receiveAsFlow()

    init {
        _state.update { it.copy(isOnEdit = heroId.isNotBlank()) }

        viewModelScope.launch {
            if (heroId.isNotBlank()) {
                databaseRepository.getHero(
                    heroId = heroId
                ).collect { either ->
                    either.onRight { hero ->
                        _state.update { currentState ->
                            currentState.copy(hero = hero, isHeroLoading = false)
                        }
                    }.onLeft { error ->
                        _state.update { currentState ->
                            currentState.copy(isHeroError = error, isHeroLoading = false)
                        }
                    }
                }
            } else {
                _state.update { it.copy(isHeroLoading = false) }
            }
        }
    }

    fun onAction(action: ManageHeroAction) {
        when (action) {
            is ManageHeroAction.HeroNameTextField -> {
                _state.update { currentState ->
                    currentState.copy(hero = currentState.hero.copy(name = action.name))
                }
            }
            ManageHeroAction.DeleteBottomSheet -> {
                _state.update { currentState ->
                    currentState.copy(isDeleteBottomSheetVisible = !currentState.isDeleteBottomSheetVisible)
                }
            }
            ManageHeroAction.DeleteButton -> {
                deleteButton()
            }
            ManageHeroAction.UpsertButton -> {
                upsertButton()
            }
        }
    }

    private fun deleteButton() {
        viewModelScope.launch {
            val hero = _state.value.hero
            databaseRepository.deleteHero(
                heroModel = hero
            ).onStart {
                _state.update { it.copy(isDeleteButtonLoading = true) }
            }.onCompletion {
                _state.update { it.copy(isDeleteButtonLoading = false) }
            }.collect { either ->
                either.onRight {
                    navigationRepository.popBackStack()
                }.onLeft { error ->
                    _effect.send(ScreenEffect.ShowSnackBar(error))
                }
            }
        }
    }

    private fun upsertButton() {
        viewModelScope.launch {
            val hero = _state.value.hero.copy(
                id = heroId.ifBlank { Uuid.random().toString() }
            )
            databaseRepository.upsertHero(
                heroModel = hero
            ).onStart {
                _state.update { it.copy(isUpsertButtonLoading = true) }
            }.onCompletion {
                _state.update { it.copy(isUpsertButtonLoading = false) }
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