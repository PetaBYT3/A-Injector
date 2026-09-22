package com.a.injector.presentation.component

import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularWavyProgressIndicator
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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

@Composable
fun CustomExtendedFloatingActionButton(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit),
    content: @Composable (() -> Unit),
    isLoading: Boolean = false
) {
    ExtendedFloatingActionButton(
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