@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package com.a.injector.presentation.component

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularWavyProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer

@Composable
fun CustomButton(
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    onClick: (() -> Unit),
    text: String,
    isLoading: Boolean = false,
    enabled: Boolean = true
) {
    val contentAlpha by animateFloatAsState(
        targetValue = if (isLoading) 0f else 1f,
        animationSpec = tween(durationMillis = 200),
        label = "ContentAlpha"
    )

    val loadingAlpha by animateFloatAsState(
        targetValue = if (isLoading) 1f else 0f,
        animationSpec = tween(durationMillis = 200),
        label = "LoadingAlpha"
    )

    Button(
        modifier = modifier,
        colors = if (isError) {
            ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.errorContainer,
                contentColor = MaterialTheme.colorScheme.onErrorContainer
            )
        } else {
            ButtonDefaults.buttonColors()
        },
        onClick = { if (!isLoading) onClick() },
        content = {
            Box(
                contentAlignment = Alignment.Center
            ) {
                Text(
                    modifier = Modifier
                        .graphicsLayer(alpha = contentAlpha),
                    text = text
                )
                CircularWavyProgressIndicator(
                    modifier = Modifier
                        .size(ButtonDefaults.IconSize)
                        .graphicsLayer(alpha = loadingAlpha)
                )
            }
        },
        enabled = enabled && !isLoading
    )
}

@Composable
fun CustomTonalButton(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit),
    text: String,
    isLoading: Boolean = false,
    enabled: Boolean = true
) {
    val contentAlpha by animateFloatAsState(
        targetValue = if (isLoading) 0f else 1f,
        animationSpec = tween(durationMillis = 200),
        label = "ContentAlpha"
    )

    val loadingAlpha by animateFloatAsState(
        targetValue = if (isLoading) 1f else 0f,
        animationSpec = tween(durationMillis = 200),
        label = "LoadingAlpha"
    )

    FilledTonalButton(
        modifier = modifier,
        onClick = { if (!isLoading) onClick() },
        content = {
            Box(
                contentAlignment = Alignment.Center
            ) {
                Text(
                    modifier = Modifier
                        .graphicsLayer(alpha = contentAlpha),
                    text = text
                )
                CircularWavyProgressIndicator(
                    modifier = Modifier
                        .size(ButtonDefaults.IconSize)
                        .graphicsLayer(alpha = loadingAlpha)
                )
            }
        },
        enabled = enabled && !isLoading
    )
}