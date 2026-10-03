package com.a.injector.presentation.component

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularWavyProgressIndicator
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun CustomFloatingActionButton(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit),
    content: ImageVector,
    isLoading: Boolean = false
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

    FloatingActionButton(
        modifier = modifier,
        onClick = { if (!isLoading) onClick() },
        content = {
            Box(
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    modifier = Modifier
                        .graphicsLayer(alpha = contentAlpha),
                    imageVector = content,
                    contentDescription = null
                )
                CircularWavyProgressIndicator(
                    modifier = Modifier
                        .size(24.dp)
                        .graphicsLayer(alpha = loadingAlpha)
                )
            }
        }
    )
}

@Composable
fun CustomExtendedFloatingActionButton(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit),
    content: String,
    isLoading: Boolean = false
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

    ExtendedFloatingActionButton(
        modifier = modifier,
        onClick = { if (!isLoading) onClick() },
        content = {
            Box(
                contentAlignment = Alignment.Center
            ) {
                Text(
                    modifier = Modifier
                        .graphicsLayer(alpha = contentAlpha),
                    text = content
                )
                CircularWavyProgressIndicator(
                    modifier = Modifier
                        .size(24.dp)
                        .graphicsLayer(alpha = loadingAlpha)
                )
            }
        }
    )
}