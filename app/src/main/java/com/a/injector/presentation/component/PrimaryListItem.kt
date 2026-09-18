package com.a.injector.presentation.component

import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedListItem
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun PrimaryListItem(
    modifier: Modifier = Modifier,
    index: Int = 0,
    count: Int = 1,
    leadingContent: @Composable (() -> Unit)? = null,
    overlineContent: @Composable (() -> Unit)? = null,
    content: @Composable (() -> Unit),
    trailingContent: @Composable (() -> Unit)? = null,
    supportingContent: @Composable (() -> Unit)? = null,
) {
    SegmentedListItem(
        modifier = modifier,
        colors = ListItemDefaults.colors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            overlineContentColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            supportingContentColor = MaterialTheme.colorScheme.primary,
            trailingContentColor = MaterialTheme.colorScheme.primary
        ),
        shapes = ListItemDefaults.segmentedShapes(
            index = index,
            count = count
        ),
        leadingContent = leadingContent,
        overlineContent = overlineContent,
        content = content,
        trailingContent = trailingContent,
        supportingContent = supportingContent
    )
}