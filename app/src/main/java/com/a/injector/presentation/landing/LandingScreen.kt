@file:OptIn(ExperimentalMaterial3Api::class)

package com.a.injector.presentation.landing

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material3.BottomSheet
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack
import com.a.injector.R
import com.a.injector.presentation.navigation.NavigationRoute
import kotlinx.coroutines.launch

@Composable
fun LandingScreen(
    navBackStack: NavBackStack<NavKey>
) {
    val scope = rememberCoroutineScope()

    val bottomSheetState = rememberBottomSheetState(
        initialValue = SheetValue.Expanded
    )

    Scaffold(
        contentWindowInsets = WindowInsets.statusBars,
        content = { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                Text(
                    modifier = Modifier
                        .padding(bottom = 50.dp)
                        .align(Alignment.Center),
                    text = stringResource(R.string.app_name),
                    style = MaterialTheme.typography.displayMedium
                )
                Button(
                    modifier = Modifier
                        .padding(bottom = 50.dp)
                        .height(ButtonDefaults.MediumContainerHeight)
                        .align(Alignment.BottomCenter),
                    onClick = {
                        scope.launch {
                            bottomSheetState.expand()
                        }
                    },
                    content = { Text(text = stringResource(R.string.action_get_started)) }
                )
                BottomSheet(
                    state = bottomSheetState
                ) {
                    Column(
                        modifier = Modifier
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        FilledTonalButton(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(ButtonDefaults.MediumContainerHeight),
                            onClick = { navBackStack.add(NavigationRoute.SignInScreen) },
                            content = { Text(text = stringResource(R.string.title_sign_in)) }
                        )
                        FilledTonalButton(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(ButtonDefaults.MediumContainerHeight),
                            onClick = { navBackStack.add(NavigationRoute.SignUpScreen) },
                            content = { Text(text = stringResource(R.string.title_sign_up)) }
                        )
                        FilledTonalButton(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(ButtonDefaults.MediumContainerHeight),
                            onClick = {  },
                            content = { Text(text = stringResource(R.string.title_guest)) }
                        )
                    }
                }
            }
        }
    )
}

@Composable
@Preview
private fun Preview() {
    LandingScreen(
        navBackStack = rememberNavBackStack()
    )
}