package com.a.injector.presentation.component

import androidx.compose.foundation.shape.AbsoluteRoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import coil.request.ImageRequest
import com.a.injector.R
import me.saket.telephoto.zoomable.coil.ZoomableAsyncImage

@Composable
fun CustomAsyncImage(
    modifier: Modifier = Modifier,
    imageSource: String
) {
    var imageState by remember {
        mutableStateOf<AsyncImagePainter.State>(AsyncImagePainter.State.Empty)
    }

    SubcomposeAsyncImage(
        modifier = modifier
            .clip(AbsoluteRoundedCornerShape(15.dp)),
        model = imageSource,
        contentDescription = null,
        contentScale = ContentScale.Fit,
        onState = { state -> imageState = state }
    ) {
        CustomFadeAnimatedContent(
            targetState = imageState
        ) { animatedContentState ->
            when (animatedContentState) {
                is AsyncImagePainter.State.Loading -> {
                    CustomCenterCircularWavyProgressIndicator()
                }
                is AsyncImagePainter.State.Error -> {
                    CustomCenterTextMessage(
                        text = stringResource(R.string.exception_unknown)
                    )
                }
                else -> {
                    SubcomposeAsyncImageContent()
                }
            }
        }
    }
}

@Composable
fun CustomZoomableAsyncImage(
    modifier: Modifier = Modifier,
    imageSource: Any?
) {
    ZoomableAsyncImage(
        modifier = modifier,
        model = ImageRequest.Builder(LocalContext.current)
            .data(imageSource)
            .crossfade(true)
            .placeholder(R.drawable.downloading)
            .error(R.drawable.warning)
            .build(),
        contentDescription = null,
        contentScale = ContentScale.Fit,
    )
}