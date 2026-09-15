package com.a.injector.presentation.component

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEachIndexed
import com.a.injector.R
import com.a.injector.data.util.toDateTime
import com.a.injector.data.util.toMegaBytes
import com.a.injector.domain.model.ReplaceModel
import com.a.injector.domain.model.SkinDetailModel

@Composable
@Preview
private fun Preview() {
    SkinDetailListItem(
        skinDetail = SkinDetailModel(
            id = "",
            heroId = "",
            label = "",
            name = "",
        )
    )
}

@Composable
fun SkinDetailListItem(
    modifier: Modifier = Modifier,
    skinDetail: SkinDetailModel,
    skinTrailingContent: @Composable (() -> Unit)? = null,
    replaceTrailingContent: @Composable ((ReplaceModel) -> Unit)? = null
) {
    Column(
        modifier = modifier
            .animateContentSize(),
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
            overlineContent = { Text(text = skinDetail.label) },
            content = { Text(text = skinDetail.name) },
            trailingContent = skinTrailingContent
        )
        skinDetail.replaces.fastForEachIndexed { index, replace ->
            SegmentedListItem(
                colors = ListItemDefaults.colors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer
                ),
                shapes = ListItemDefaults.segmentedShapes(
                    index = index + 1,
                    count = skinDetail.replaces.size + 1
                ),
                overlineContent = { Text(text = "${stringResource(R.string.title_replace)} ${replace.label}") },
                content = { Text(text = replace.name) },
                supportingContent = {
                    Text(
                        text = "${replace.lastUpdate?.toDateTime()} | ${replace.fileSize?.toMegaBytes()}"
                    )
                },
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