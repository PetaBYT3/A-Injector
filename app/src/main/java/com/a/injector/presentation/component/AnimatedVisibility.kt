package com.a.injector.presentation.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

private const val transitionDuration = 300

@Composable
fun CustomSlideUpAnimatedVisibility(
    modifier: Modifier = Modifier,
    visible: Boolean,
    content: @Composable (AnimatedVisibilityScope.() -> Unit)
) {
    AnimatedVisibility(
        modifier = modifier,
        enter = slideInVertically(
            initialOffsetY = { it },
            animationSpec = tween(durationMillis = 300)
        ) + fadeIn(animationSpec = tween(300)),
        exit = slideOutVertically(
            targetOffsetY = { it },
            animationSpec = tween(durationMillis = 300)
        ) + fadeOut(animationSpec = tween(300)),
        visible = visible,
        content = content
    )
}