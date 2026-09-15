package com.a.injector.presentation.hero

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.a.injector.domain.model.ReplaceModel
import com.a.injector.domain.repository.DatabaseRepository
import com.a.injector.domain.repository.InjectRepository
import com.a.injector.presentation.util.ScreenEffect
import kotlinx.coroutines.Job
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
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap

@KoinViewModel
class HeroViewModel(
    @InjectedParam private val heroId: String,
    private val databaseRepository: DatabaseRepository,
    private val injectRepository: InjectRepository
): ViewModel() {
    private val _state = MutableStateFlow(HeroState())
    val state = _state.asStateFlow()

    private val _effect = Channel<ScreenEffect>()
    val effect = _effect.receiveAsFlow()

    private val injectJob: ConcurrentMap<String, Job> = ConcurrentHashMap()

    init {
        viewModelScope.launch {
            databaseRepository.getHeroDetail(
                id = heroId
            ).collect { either ->
                either.onRight { heroDetailModel ->
                    _state.update {
                        it.copy(heroDetail = heroDetailModel, isHeroDetailLoading = false)
                    }
                }.onLeft { error ->
                    _state.update {
                        it.copy(isHeroDetailError = error, isHeroDetailLoading = false)
                    }
                }
            }
        }
    }

    fun onAction(action: HeroAction) {
        when (action) {
            is HeroAction.ShowActionSkinBottomSheet -> {
                _state.update { it.copy(isActionSkinBottomSheetVisible = true, skinToAction = action.skin) }
            }
            HeroAction.DismissSkinActionBottomSheet -> {
                _state.update { it.copy(isActionSkinBottomSheetVisible = false) }
            }
            is HeroAction.StartInject -> {
                startInject(replace = action.replace)
            }
        }
    }

    private fun startInject(replace: ReplaceModel) {
        injectJob[replace.id] = viewModelScope.launch {
            injectRepository.start(
                replaceModel = replace
            ).onStart {
                _state.update { it.copy(isInjectLoading = it.isInjectLoading + (replace.id to Unit)) }
            }.onCompletion {
                injectJob.remove(replace.id)
                _state.update { it.copy(isInjectLoading = it.isInjectLoading - replace.id) }
            }.collect { either ->
                either.onRight { message ->
                    _effect.send(ScreenEffect.ShowSnackBar(message))
                }.onLeft { error ->
                    _effect.send(ScreenEffect.ShowSnackBar(error))
                }
            }
        }
    }
}