package com.a.injector.presentation.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.InsertDriveFile
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEachIndexed
import com.a.injector.R
import com.a.injector.data.util.toDateTime
import com.a.injector.data.util.toMegaBytes
import com.a.injector.domain.model.ReplaceModel
import com.a.injector.domain.model.SkinModel
import kotlin.uuid.Uuid

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
            leadingContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
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

@Composable
fun PrimaryClickableListItem(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit),
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
            leadingContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            overlineContentColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            supportingContentColor = MaterialTheme.colorScheme.primary,
            trailingContentColor = MaterialTheme.colorScheme.primary
        ),
        shapes = ListItemDefaults.segmentedShapes(
            index = index,
            count = count
        ),
        onClick = onClick,
        leadingContent = leadingContent,
        overlineContent = overlineContent,
        content = content,
        trailingContent = trailingContent,
        supportingContent = supportingContent
    )
}

@Composable
fun DefaultListItem(
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
            containerColor = MaterialTheme.colorScheme.surfaceContainer
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

@Composable
fun DefaultClickableListItem(
    modifier: Modifier = Modifier,
    index: Int = 0,
    count: Int = 1,
    onClick: () -> Unit,
    leadingContent: @Composable (() -> Unit)? = null,
    overlineContent: @Composable (() -> Unit)? = null,
    content: @Composable (() -> Unit),
    trailingContent: @Composable (() -> Unit)? = null,
    supportingContent: @Composable (() -> Unit)? = null,
) {
    SegmentedListItem(
        modifier = modifier,
        colors = ListItemDefaults.colors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        ),
        shapes = ListItemDefaults.segmentedShapes(
            index = index,
            count = count
        ),
        onClick = onClick,
        leadingContent = leadingContent,
        overlineContent = overlineContent,
        content = content,
        trailingContent = trailingContent,
        supportingContent = supportingContent
    )
}

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
fun SkinDetailListItem(
    modifier: Modifier = Modifier,
    skinDetail: SkinModel,
    skinTrailingContent: @Composable (() -> Unit)? = null,
    replaceTrailingContent: @Composable ((ReplaceModel) -> Unit)? = null
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(2.5.dp)
    ) {
        SegmentedListItem(
            colors = ListItemDefaults.colors(
                containerColor = MaterialTheme.colorScheme.surfaceContainer
            ),
            shapes = ListItemDefaults.segmentedShapes(
                index = 0,
                count = skinDetail.replaces.size + 1
            ),
            leadingContent = { Icon(ImageVector.vectorResource(R.drawable.skin), null) },
            content = { Text(text = skinDetail.label) },
            supportingContent = { Text(text = skinDetail.name) },
            trailingContent = skinTrailingContent
        )
        AnimatedVisibility(
            visible = skinDetail.replaces.isNotEmpty()
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(2.5.dp)
            ) {
                skinDetail.replaces.fastForEachIndexed { index, replace ->
                    val isFileExist = replace.lastUpdate != null || replace.fileSize != null
                    SegmentedListItem(
                        colors = ListItemDefaults.colors(
                            containerColor = MaterialTheme.colorScheme.surfaceContainer
                        ),
                        shapes = ListItemDefaults.segmentedShapes(
                            index = index + 1,
                            count = skinDetail.replaces.size + 1
                        ),
                        leadingContent = { Icon(Icons.Rounded.InsertDriveFile, null) },
                        overlineContent = {
                            val dateAndSize = if (isFileExist) {
                                "${replace.lastUpdate?.toDateTime()} | ${replace.fileSize?.toMegaBytes()}"
                            } else {
                                stringResource(R.string.item_no_file)
                            }
                            Text(
                                text = dateAndSize,
                                style = MaterialTheme.typography.labelSmall
                            )
                        },
                        content = { Text(text = replace.label) },
                        supportingContent = { Text(text = replace.name) },
                        trailingContent = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                replaceTrailingContent?.invoke(replace)
                            }
                        }
                    )
                }
            }
        }
    }
}

fun LazyListScope.spacer(size: Dp = 15.dp) {
    item(Uuid.random().toString()) {
        Spacer(modifier = Modifier.animateItem().height(size).width(size))
    }
}