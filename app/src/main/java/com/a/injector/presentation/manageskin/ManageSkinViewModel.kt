package com.a.injector.presentation.manageskin

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
class ManageSkinViewModel(
    @InjectedParam private val heroId: String,
    @InjectedParam private val skinId: String,
    private val databaseRepository: DatabaseRepository,
    private val navigationRepository: NavigationRepository
): ViewModel() {
    private val _state = MutableStateFlow(ManageSkinState())
    val state = _state.asStateFlow()

    private val _effect = Channel<ScreenEffect>()
    val effect = _effect.receiveAsFlow()

    init {
        _state.update { it.copy(isOnEdit = skinId.isNotBlank()) }

        viewModelScope.launch {
            databaseRepository.getHero(
                heroId = heroId
            ).collect { either ->
                either.onRight { hero ->
                    _state.update { it.copy(hero = hero, isHeroLoading = false) }
                }.onLeft { error ->
                    navigationRepository.popBackStack()
                }
            }
        }

        viewModelScope.launch {
            if (skinId.isNotBlank()) {
                databaseRepository.getSkin(
                    skinId = skinId
                ).collect { either ->
                    either.onRight { skin ->
                        _state.update { it.copy(skin = skin, isSkinLoading = false) }
                    }.onLeft { error ->
                        navigationRepository.popBackStack()
                    }
                }
            } else {
                _state.update { it.copy(isSkinLoading = false) }
            }
        }
    }

    fun onAction(action: ManageSkinAction) {
        when (action) {
            is ManageSkinAction.SkinLabelTextField -> {
                _state.update { it.copy(skin = it.skin.copy(label = action.label)) }
            }
            is ManageSkinAction.SkinNameTextField -> {
                _state.update { it.copy(skin = it.skin.copy(name = action.name)) }
            }
            ManageSkinAction.DeleteBottomSheet -> {
                _state.update { it.copy(isDeleteBottomSheetVisible = !it.isDeleteBottomSheetVisible) }
            }
            ManageSkinAction.DeleteButton -> {
                deleteButton()
            }
            ManageSkinAction.UpsertButton ->{
                upsertButton()
            }
        }
    }

    private fun deleteButton() {
        viewModelScope.launch {
            val skin = _state.value.skin
            databaseRepository.deleteSkin(
                skinModel = skin
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
            val skin = _state.value.skin.copy(
                id = skinId.ifBlank { Uuid.random().toString() },
                heroId = heroId
            )
            databaseRepository.upsertSkin(
                skinModel = skin
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