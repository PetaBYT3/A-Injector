@file:OptIn(ExperimentalMaterial3Api::class)

package com.a.injector.presentation.util

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@Composable
fun CustomUndismissableBottomSheet(
    modifier: Modifier = Modifier,
    visible: Boolean,
    title: String,
    content: (LazyListScope.() -> Unit)
) {
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true,
        confirmValueChange = {
            if (visible) it != SheetValue.Hidden else true
        }
    )

    LaunchedEffect(visible) {
        scope.launch {
            if (visible) sheetState.hide()
        }
    }

    if (visible) {
        ModalBottomSheet(
            modifier = modifier
                .statusBarsPadding(),
            sheetState = sheetState,
            onDismissRequest = {  }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                CustomTopAppBar(
                    title = title
                )
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(2.5.dp)
                ) {
                    content()
                }
            }
        }
    }
}

@Composable
fun CustomBottomSheet(
    modifier: Modifier = Modifier,
    visible: Boolean,
    onDismiss: () -> Unit,
    title: String,
    content: (LazyListScope.(dismissSheet: () -> Unit) -> Unit),
    bottomBar: @Composable ((dismissSheet: () -> Unit) -> Unit)? = null
) {
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    var isSheetVisible by remember {
        mutableStateOf(visible)
    }

    LaunchedEffect(visible) {
        if (visible) {
            isSheetVisible = true
        } else {
            if (isSheetVisible) {
                sheetState.hide()
                isSheetVisible = false
            }
        }
    }

    val dismissSheet: () -> Unit = {
        scope.launch {
            sheetState.hide()
            onDismiss()
        }
    }

    if (isSheetVisible) {
        ModalBottomSheet(
            modifier = modifier
                .statusBarsPadding(),
            sheetState = sheetState,
            onDismissRequest = { dismissSheet() }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                CustomTopAppBar(
                    title = title
                )
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(2.5.dp)
                ) {
                    content(dismissSheet)
                }
                if (bottomBar != null) {
                    BottomAppBar(
                        containerColor = Color.Transparent
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 10.dp),
                            horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.End),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            bottomBar(dismissSheet)
                        }
                    }
                }
            }
        }
    }
}