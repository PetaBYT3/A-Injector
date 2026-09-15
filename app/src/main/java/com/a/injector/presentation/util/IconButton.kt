package com.a.injector.presentation.util

import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularWavyProgressIndicator
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun CustomIconButton(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit),
    content: @Composable (() -> Unit),
    isLoading: Boolean = false
) {
    IconButton(
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
        }
    )
}