package com.a.injector.presentation.supportmethod

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.a.injector.domain.repository.MediaRepository
import com.a.injector.presentation.util.ScreenEffect
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class SupportMethodViewModel(
    private val mediaRepository: MediaRepository
): ViewModel() {
    private val _state = MutableStateFlow(SupportMethodState())
    val state = _state.asStateFlow()

    private val _effect = Channel<ScreenEffect>()
    val effect = _effect.receiveAsFlow()

    fun onAction(action: SupportMethodAction) {
        when (action) {
            SupportMethodAction.QrisBottomSheet -> {
                _state.update { currentState ->
                    currentState.copy(
                        isQrisBottomSheetVisible = !currentState.isQrisBottomSheetVisible
                    )
                }
            }
            is SupportMethodAction.DownloadDrawable -> {
                downloadDrawable(drawable = action.drawable)
            }
        }
    }

    private fun downloadDrawable(drawable: Int) {
        viewModelScope.launch {
            mediaRepository.downloadDrawable(
                drawable = drawable,
                fileName = "A Injector QRIS"
            ).collect { either ->
                either.onRight { text ->
                    _effect.send(ScreenEffect.ShowSnackBar(text))
                }.onLeft { text ->
                    _effect.send(ScreenEffect.ShowSnackBar(text))
                }
            }
        }
    }
}