package com.a.injector.presentation.util

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun CustomTextListTitle(
    modifier: Modifier = Modifier,
    text: String
) {
    Text(
        modifier = modifier
            .padding(start = 16.dp, end = 16.dp, bottom = 5.dp),
        color = MaterialTheme.colorScheme.primary,
        style = MaterialTheme.typography.bodyMedium,
        text = text
    )
}

@Composable
fun CustomCenterTextMessage(
    modifier: Modifier = Modifier,
    text: String
) {
    Box(
        modifier = modifier
            .fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            text = text
        )
    }
}

@Composable
fun CustomSurfaceText(
    modifier: Modifier = Modifier,
    text: String
) {
    Text(
        modifier = modifier
            .padding(start = 6.dp, end = 6.dp),
        style = MaterialTheme.typography.bodyMedium,
        text = text
    )
}