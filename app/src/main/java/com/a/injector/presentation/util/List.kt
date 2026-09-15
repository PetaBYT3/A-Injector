package com.a.injector.presentation.util

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.uuid.Uuid

fun LazyListScope.spacer(size: Dp = 15.dp) {
    item(Uuid.random().toString()) {
        Spacer(modifier = Modifier.animateItem().height(size).width(size))
    }
}