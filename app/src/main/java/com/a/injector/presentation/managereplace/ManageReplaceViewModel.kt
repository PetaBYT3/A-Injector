package com.a.injector.presentation.managereplace

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
class ManageReplaceViewModel(
    @InjectedParam private val heroId: String,
    @InjectedParam private val skinId: String,
    @InjectedParam private val replaceId: String,
    private val databaseRepository: DatabaseRepository,
    private val navigationRepository: NavigationRepository
): ViewModel() {
    private val _state = MutableStateFlow(ManageReplaceState())
    val state = _state.asStateFlow()

    private val _effect = Channel<ScreenEffect>()
    val effect = _effect.receiveAsFlow()

    init {
        _state.update { it.copy(isOnEdit = replaceId.isNotBlank()) }

        viewModelScope.launch {
            databaseRepository.getHero(
                heroId = heroId
            ).collect { either ->
                either.onRight { heroModel ->
                    _state.update { currentState ->
                        currentState.copy(hero = heroModel, isHeroLoading = false)
                    }
                }.onLeft { error ->
                    navigationRepository.popBackStack()
                    _state.update { currentState ->
                        currentState.copy(isHeroError = error, isHeroLoading = false)
                    }
                }
            }
        }

        viewModelScope.launch {
            databaseRepository.getSkin(
                skinId = skinId
            ).collect { either ->
                either.onRight { skinModel ->
                    _state.update { currentState ->
                        currentState.copy(skin = skinModel, isSkinLoading = false)
                    }
                }.onLeft { error ->
                    navigationRepository.popBackStack()
                    _state.update { currentState ->
                        currentState.copy(isSkinError = error, isSkinLoading = false)
                    }
                }
            }
        }

        viewModelScope.launch {
            if (replaceId.isNotBlank()) {
                databaseRepository.getReplace(
                    replaceId = replaceId
                ).collect { either ->
                    either.onRight { replaceModel ->
                        _state.update { currentState ->
                            currentState.copy(replace = replaceModel, isReplaceLoading = false)
                        }
                    }.onLeft { error ->
                        navigationRepository.popBackStack()
                        _state.update { currentState ->
                            currentState.copy(isReplaceError = error, isReplaceLoading = false)
                        }
                    }
                }
            } else {
                _state.update { it.copy(isReplaceLoading = false) }
            }
        }
    }

    fun onAction(action: ManageReplaceAction) {
        when (action) {
            is ManageReplaceAction.LabelTextField -> {
                _state.update { currentState ->
                    currentState.copy(replace = currentState.replace.copy(label = action.label))
                }
            }
            is ManageReplaceAction.NameTextField -> {
                _state.update { currentState ->
                    currentState.copy(replace = currentState.replace.copy(name = action.name))
                }
            }
            is ManageReplaceAction.ReplaceFilePicker -> {
                _state.update { currentState ->
                    currentState.copy(replaceFile = action.platformFile)
                }
            }
            ManageReplaceAction.DeleteBottomSheet -> {
                _state.update { currentState ->
                    currentState.copy(isDeleteBottomSheetVisible = !currentState.isDeleteBottomSheetVisible)
                }
            }
            ManageReplaceAction.DeleteButton -> {
                deleteButton()
            }
            ManageReplaceAction.UpsertButton -> {
                upsertButton()
            }
        }
    }

    private fun deleteButton() {
        viewModelScope.launch {
            val script = _state.value.replace
            databaseRepository.deleteReplace(
                replaceModel = script
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
            val scriptFile = _state.value.replaceFile
            val script = _state.value.replace.copy(
                id = replaceId.ifBlank { Uuid.random().toString() },
                skinId = skinId
            )
            databaseRepository.upsertReplace(
                replaceModel = script,
                platformFile = scriptFile
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