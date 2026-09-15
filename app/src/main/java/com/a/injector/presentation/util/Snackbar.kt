package com.a.injector.presentation.util

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.flow.Flow

@Composable
fun SnackBarEffectLauncher(
    snackBarHostState: SnackbarHostState,
    screenEffect: Flow<ScreenEffect>
) {
    val lifeCycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(lifeCycleOwner.lifecycle, screenEffect) {
        lifeCycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            screenEffect.collect { effect ->
                when (effect) {
                    is ScreenEffect.ShowSnackBar -> {
                        snackBarHostState.showSnackbar(
                            message = effect.message,
                            withDismissAction = true
                        )
                    }
                }
            }
        }
    }
}