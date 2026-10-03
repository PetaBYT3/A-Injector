@file:SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")

package com.a.injector.presentation.imagepreview

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.a.injector.presentation.component.CustomAsyncImage
import com.a.injector.presentation.mainnavigation.popBackStack

@Composable
fun ImagePreviewScreenRoot(
    navBackStack: NavBackStack<NavKey>,
    imageSource: String
) {
    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                ),
                navigationIcon = {
                    FilledTonalIconButton(
                        onClick = { navBackStack.popBackStack() },
                        content = { Icon(Icons.Rounded.ArrowBack, null) }
                    )
                },
                title = { Text(text = "") }
            )
        },
        content = { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CustomAsyncImage(
                    modifier = Modifier
                        .fillMaxSize(),
                    imageSource = imageSource
                )
            }
        }
    )
}