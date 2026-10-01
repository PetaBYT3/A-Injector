package com.a.injector.presentation.component

import androidx.compose.foundation.shape.AbsoluteRoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.a.injector.R
import me.saket.telephoto.zoomable.coil.ZoomableAsyncImage

@Composable
fun CustomAsyncImage(
    modifier: Modifier = Modifier,
    imageSource: String
) {
    AsyncImage(
        modifier = modifier
            .clip(AbsoluteRoundedCornerShape(15.dp)),
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