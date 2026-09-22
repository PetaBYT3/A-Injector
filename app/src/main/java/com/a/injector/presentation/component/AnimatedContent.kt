package com.a.injector.presentation.component

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun <S> CustomFadeAnimatedContent(
    modifier: Modifier = Modifier,
    targetState: S,
    content: @Composable (AnimatedContentScope.(S) -> Unit)
) {
    AnimatedContent(
        modifier = modifier,
        transitionSpec = {
            fadeIn(tween(300)) togetherWith fadeOut(tween(300))
        },
        targetState = targetState,
        content = content
    )
}