package com.a.injector.presentation.component

import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark

@Composable
fun ErrorListItem(
    modifier: Modifier = Modifier,
    text: String
) {
    SegmentedListItem(
        modifier = modifier,
        colors = ListItemDefaults.colors(
            containerColor = MaterialTheme.colorScheme.errorContainer,
            contentColor = MaterialTheme.colorScheme.onErrorContainer
        ),
        shapes = ListItemDefaults.segmentedShapes(
            index = 0,
            count = 1
        ),
        content = {
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    )
}

@Composable
@PreviewLightDark
private fun Preview() {
    ErrorListItem(
        text = "Preview Error Message"
    )
}