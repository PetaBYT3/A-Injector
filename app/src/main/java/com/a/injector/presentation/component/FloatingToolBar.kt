package com.a.injector.presentation.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalFloatingToolbar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun CustomFloatingActionToolBar(
    modifier: Modifier = Modifier,
    floatingActionButton: @Composable (() -> Unit)? = null,
    content: @Composable (() -> Unit)? = null
) {
    if (content != null) {
        if (floatingActionButton != null) {
            HorizontalFloatingToolbar(
                modifier = modifier,
                expanded = true,
                floatingActionButton = { floatingActionButton() },
                content = { content.invoke() }
            )
        } else {
            HorizontalFloatingToolbar(
                modifier = modifier,
                expanded = true,
                content = { content.invoke() }
            )
        }
    } else {
        Box(
            modifier = Modifier
                .padding(bottom = 12.dp)
        ) {
            floatingActionButton?.invoke()
        }
    }
}