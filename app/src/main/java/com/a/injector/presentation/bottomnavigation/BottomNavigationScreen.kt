@file:OptIn(ExperimentalLayoutApi::class)

package com.a.injector.presentation.bottomnavigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.InsertDriveFile
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.util.fastForEachIndexed
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack
import com.a.injector.R
import com.a.injector.presentation.account.AccountScreen
import com.a.injector.presentation.component.CustomBottomSheet
import com.a.injector.presentation.component.CustomButton
import com.a.injector.presentation.component.CustomSurfaceText
import com.a.injector.presentation.component.ScreenEffectLauncher
import com.a.injector.presentation.home.HomeScreen
import com.a.injector.presentation.script.ScriptScreen
import com.a.injector.presentation.settings.SettingsScreen
import com.a.injector.presentation.util.openInBrowser
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun BottomNavigationScreen(
    navBackStack: NavBackStack<NavKey>,
    viewModel: BottomNavigationViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackBarHostState = remember { SnackbarHostState() }

    Screen(
        navBackStack = navBackStack,
        state = state,
        onAction = viewModel::onAction
    )

    ScreenEffectLauncher(
        snackBarHostState = snackBarHostState,
        screenEffect = viewModel.effect
    )
}

@Composable
@Preview
private fun Preview() {
    Screen(
        navBackStack = rememberNavBackStack(),
        state = BottomNavigationState(),
        onAction = {}
    )
}

@Composable
private fun Screen(
    navBackStack: NavBackStack<NavKey>,
    state: BottomNavigationState,
    onAction: (BottomNavigationAction) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val isKeyboardVisible = WindowInsets.isImeVisible
    val uriHandler = LocalUriHandler.current

    val pagerState = rememberPagerState(
        pageCount = { bottomNavigationItems.size }
    )
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        HorizontalPager(
            modifier = Modifier
                .weight(1f),
            state = pagerState,
        ) { pageContent ->
            when (pageContent) {
                0 -> HomeScreen(navBackStack = navBackStack)
                1 -> ScriptScreen(navBackStack = navBackStack)
                2 -> AccountScreen(navBackStack = navBackStack)
                3 -> SettingsScreen(navBackStack = navBackStack)
            }
        }
        AnimatedVisibility(
            visible = !isKeyboardVisible
        ) {
            NavigationBar(
                content = {
                    bottomNavigationItems.fastForEachIndexed { index, staticModel ->
                        NavigationBarItem(
                            selected = pagerState.currentPage == index,
                            onClick = {
                                scope.launch {
                                    pagerState.animateScrollToPage(index)
                                }
                            },
                            icon = {
                                val imageVector = when (staticModel.id) {
                                    BottomNavigationId.Home -> Icons.Rounded.Home
                                    BottomNavigationId.Script -> Icons.Rounded.InsertDriveFile
                                    BottomNavigationId.Account -> Icons.Rounded.Person
                                    BottomNavigationId.Settings -> Icons.Rounded.Settings
                                }
                                Icon(imageVector, null)
                            },
                            label = { Text(text = stringResource(staticModel.contentTextResId)) }
                        )
                    }
                }
            )
        }
    }

    CustomBottomSheet(
        visible = state.isMaintenanceBottomSheetVisible,
        onDismiss = { onAction(BottomNavigationAction.MaintenanceBottomSheet) },
        title = stringResource(R.string.action_maintenance),
        content = {
            item {
                CustomSurfaceText(text = stringResource(R.string.message_maintenance))
            }
        },
        bottomBar = {
            CustomButton(
                onClick = { onAction(BottomNavigationAction.MaintenanceBottomSheet) },
                text = stringResource(R.string.action_dismiss)
            )
        }
    )

    CustomBottomSheet(
        visible = state.isUpdateBottomSheetVisible,
        onDismiss = { onAction(BottomNavigationAction.UpdateBottomSheet) },
        title = stringResource(R.string.action_update),
        content = {
            item {
                CustomSurfaceText(text = stringResource(R.string.message_update))
            }
        },
        bottomBar = {
            CustomButton(
                onClick = {
                    onAction(BottomNavigationAction.UpdateBottomSheet)
                    openInBrowser(
                        context = context,
                        uriHandler = uriHandler,
                        url = "https://github.com/PetaBYT3/A-Injector/releases"
                    )
                },
                text = stringResource(R.string.action_confirm)
            )
        }
    )
}