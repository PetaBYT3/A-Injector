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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.a.injector.R
import com.a.injector.presentation.component.CustomSurfaceText
import com.a.injector.presentation.component.CustomUndismissableBottomSheet
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun LoadingScreen(
    navBackStack: NavBackStack<NavKey>,
    viewModel: LoadingViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Screen(
        state = state
    )
}

@Composable
private fun Screen(
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

    CustomUndismissableBottomSheet(
        visible = state.isMaintenanceBottomSheetVisible,
        title = stringResource(R.string.action_maintenance),
        content = {
            item {
                CustomSurfaceText(text = stringResource(R.string.lorem_ipsum))
            }
        },
    )

    CustomUndismissableBottomSheet(
        visible = state.isUpdateBottomSheetVisible,
        title = stringResource(R.string.action_update),
        content = {
            item {
                CustomSurfaceText(text = stringResource(R.string.lorem_ipsum))
            }
        }
    )
}

@Composable
@Preview
private fun Preview() {
    Screen(
        state = LoadingState()
    )
}