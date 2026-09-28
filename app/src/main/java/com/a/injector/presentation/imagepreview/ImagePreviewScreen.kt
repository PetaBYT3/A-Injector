package com.a.injector.presentation.imagepreview

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.a.injector.presentation.component.CustomAsyncImage
import com.a.injector.presentation.component.CustomTopAppBar
import com.a.injector.presentation.mainnavigation.popBackStack

@Composable
fun ImagePreviewScreenRoot(
    navBackStack: NavBackStack<NavKey>,
    imageUrl: String
) {
    Scaffold(
        topBar = {
            CustomTopAppBar(
                navigationClick = { navBackStack.popBackStack() },
                title = ""
            )
        },
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
            ) {
                CustomAsyncImage(
                    modifier = Modifier
                        .fillMaxWidth(),
                    imageUrl = imageUrl
                )
            }
        }
    )
}