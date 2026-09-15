package com.a.injector.presentation.util

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularWavyProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.WavyProgressIndicatorDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp

@Composable
fun CustomFloatingActionButton(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit),
    content: @Composable (() -> Unit),
    isLoading: Boolean = false
) {
    FloatingActionButton(
        modifier = modifier,
        onClick = { if (!isLoading) onClick() },
        content = {
            CustomFadeAnimatedContent(
                targetState = isLoading
            ) { targetState ->
                when (targetState) {
                    true -> {
                        CircularWavyProgressIndicator(
                            modifier = Modifier
                                .size(24.dp)
                        )
                    }
                    false -> {
                        content()
                    }
                }
            }
        },
    )
}