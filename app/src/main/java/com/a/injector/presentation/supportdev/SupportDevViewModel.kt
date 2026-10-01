package com.a.injector.presentation.supportdev

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class SupportDevViewModel: ViewModel() {
    private val _state = MutableStateFlow(SupportDevState())
    val state = _state.asStateFlow()

    fun onAction(action: SupportDevAction) {
        when (action) {
            SupportDevAction.QrisBottomSheet -> {
                _state.update { currentState ->
                    currentState.copy(
                        isQrisBottomSheetVisible = !currentState.isQrisBottomSheetVisible
                    )
                }
            }
        }
    }
}