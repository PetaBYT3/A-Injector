package com.a.injector.presentation.util

import android.widget.Toast
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.flow.Flow

@Composable
fun ScreenEffectLauncher(
    snackBarHostState: SnackbarHostState,
    screenEffect: Flow<ScreenEffect>
) {
    val context = LocalContext.current
    val lifeCycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(lifeCycleOwner.lifecycle, screenEffect) {
        lifeCycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            screenEffect.collect { effect ->
                when (effect) {
                    is ScreenEffect.ShowSnackBar -> {
                        snackBarHostState.showSnackbar(
                            message = effect.message.asString(context),
                            withDismissAction = true
                        )
                    }
                    is ScreenEffect.ShowToast -> {
                        Toast.makeText(context, effect.message.asString(context), Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}