package com.a.injector.presentation.script

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.a.injector.data.dto.Role
import com.a.injector.domain.repository.AccountRepository
import com.a.injector.domain.repository.DatabaseRepository
import com.a.injector.presentation.util.ScreenEffect
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class ScriptViewModel(
    private val accountRepository: AccountRepository,
    private val databaseRepository: DatabaseRepository
): ViewModel() {
    private val _state = MutableStateFlow(ScriptState())
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
            databaseRepository.getHeroes().collect { either ->
                either.onRight { heroes ->
                    _state.update { currentState ->
                        currentState.copy(heroes = heroes, isHeroesLoading = false)
                    }
                    searchTextFiled(keyword = "")
                }.onLeft { error ->
                    _state.update { currentState ->
                        currentState.copy(isHeroesError = error, isHeroesLoading = false)
                    }
                }
            }
        }
    }

    fun onAction(action: ScriptAction) {
        when (action) {
            is ScriptAction.SearchTextField -> {
                searchTextFiled(keyword = action.keyword)
            }
        }
    }

    private fun searchTextFiled(keyword: String) {
        val filteredHeroes = if (keyword.isBlank()) {
            _state.value.heroes
        } else {
            _state.value.heroes.filter { hero ->
                hero.name.contains(keyword, true)
            }
        }
        _state.update { currentState ->
            currentState.copy(searchTextField = keyword, filteredHeroes = filteredHeroes)
        }
    }
}