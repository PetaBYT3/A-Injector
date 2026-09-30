package com.a.injector.presentation.loading

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularWavyProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun LoadingScreenRoot(
    navBackStack: NavBackStack<NavKey>,
    signMethod: SignMethod,
    viewModel: LoadingViewModel = koinViewModel(
        parameters = {
            parametersOf(signMethod)
        }
    )
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LoadingScreen(
        state = state
    )
}

@Composable
private fun LoadingScreen(
    state: LoadingState
) {
    BackHandler(enabled = true) {}

    Scaffold(
        content = { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularWavyProgressIndicator()
            }
        }
    )
}

@Composable
@Preview
private fun Preview() {
    LoadingScreen(
        state = LoadingState()
    )
}