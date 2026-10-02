package com.a.injector.presentation.debug

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.a.injector.data.system.adb.AdbApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class DebugViewModel(
    private val adbApi: AdbApi
): ViewModel() {
    private val _state = MutableStateFlow(DebugState())
    val state = _state.asStateFlow()

    fun onAction(action: DebugAction) {
        when (action) {
            DebugAction.Start -> {
                viewModelScope.launch(Dispatchers.IO) {
                    _state.update { it.copy(isStartButtonLoading = true) }
                    adbApi.connect()
                }.invokeOnCompletion {
                    _state.update { it.copy(isStartButtonLoading = false) }
                }
            }
            DebugAction.Run -> {
                viewModelScope.launch(Dispatchers.IO) {
                    try {
                        val result = adbApi.executeCommand(_state.value.textField)
                        _state.update { it.copy(output = result) }
                    } catch (e: Exception) {
                        _state.update { it.copy(output = "Command Failed: ${e.message}") }
                    }
                }
            }
            is DebugAction.TextField -> {
                _state.update { it.copy(textField = action.text) }
            }
        }
    }
}