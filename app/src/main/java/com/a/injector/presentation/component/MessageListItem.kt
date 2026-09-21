package com.a.injector.presentation.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.PreviewLightDark

@Composable
fun MessageListItem(
    modifier: Modifier = Modifier,
    text: String,
    isError: Boolean = false
) {
    SegmentedListItem(
        modifier = modifier,
        colors = ListItemDefaults.colors(
            contentColor = Color.Transparent
        ),
        shapes = ListItemDefaults.segmentedShapes(
            index = 0,
            count = 1
        ),
        content = {
            Box(
                modifier = Modifier
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    color = if (isError) {
                        MaterialTheme.colorScheme.error
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    },
                    text = text
                )
            }
        }
    )
}

@Composable
@PreviewLightDark
private fun Preview() {
    MessageListItem(
        text = "Preview Error Message",
        isError = false
    )
}