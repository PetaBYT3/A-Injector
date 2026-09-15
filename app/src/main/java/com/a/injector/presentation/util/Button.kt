@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package com.a.injector.presentation.util

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularWavyProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer

@Composable
fun CustomButton(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit),
    text: String,
    isLoading: Boolean = false,
    enabled: Boolean = true
) {
    Button(
        modifier = modifier,
        onClick = { if (!isLoading) onClick() },
        content = {
            Box(
                contentAlignment = Alignment.Center
            ) {
                Text(
                    modifier = Modifier
                        .graphicsLayer(
                            alpha = if (isLoading) 0f else 1f
                        ),
                    text = text
                )
                CircularWavyProgressIndicator(
                    modifier = Modifier
                        .size(ButtonDefaults.IconSize)
                        .graphicsLayer(alpha = if (isLoading) 1f else 0f)
                )
            }
        },
        enabled = enabled
    )
}