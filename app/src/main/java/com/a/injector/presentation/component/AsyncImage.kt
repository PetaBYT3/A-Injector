package com.a.injector.presentation.component

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.shape.AbsoluteRoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.a.injector.R

@Composable
fun CustomAsyncImage(
    modifier: Modifier = Modifier,
    imageUrl: String
) {
    SubcomposeAsyncImage(
        modifier = modifier
            .clip(AbsoluteRoundedCornerShape(15.dp)),
        model = ImageRequest.Builder(LocalContext.current)
            .data(imageUrl)
            .memoryCachePolicy(CachePolicy.DISABLED)
            .diskCachePolicy(CachePolicy.DISABLED)
            .build(),
        contentDescription = null,
        contentScale = ContentScale.FillWidth
    ) {
        AnimatedContent(
            targetState = painter.state
        ) { animatedContentState ->
            when (animatedContentState) {
                is AsyncImagePainter.State.Loading -> {
                    CustomCenterCircularWavyProgressIndicator()
                }
                is AsyncImagePainter.State.Error -> {
                    CustomCenterTextMessage(
                        text = stringResource(R.string.exception_server_error)
                    )
                }
                else -> SubcomposeAsyncImageContent()
            }
        }
    }
}