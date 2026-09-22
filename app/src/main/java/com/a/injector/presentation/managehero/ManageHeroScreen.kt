package com.a.injector.presentation.managehero

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Save
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack
import com.a.injector.R
import com.a.injector.presentation.component.CustomBottomSheet
import com.a.injector.presentation.component.CustomButton
import com.a.injector.presentation.component.CustomCenterCircularWavyProgressIndicator
import com.a.injector.presentation.component.CustomFloatingActionButton
import com.a.injector.presentation.component.CustomFloatingActionToolBar
import com.a.injector.presentation.component.CustomIconButton
import com.a.injector.presentation.component.CustomSlideUpAnimatedVisibility
import com.a.injector.presentation.component.CustomSurfaceText
import com.a.injector.presentation.component.CustomTextField
import com.a.injector.presentation.component.CustomTextListTitle
import com.a.injector.presentation.component.CustomTopAppBar
import com.a.injector.presentation.component.ScreenEffectLauncher
import com.a.injector.presentation.navigation.popBackStack
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun ManageHeroScreen(
    navBackStack: NavBackStack<NavKey>,
    heroId: String,
    viewModel: ManageHeroViewModel = koinViewModel(
        parameters = {
            parametersOf(heroId)
        }
    )
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val onAction = viewModel::onAction
    val snackBarHostState = remember { SnackbarHostState() }

    Screen(
        navBackStack = navBackStack,
        state = state,
        onAction = onAction,
        snackBarHostState = snackBarHostState
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
        state = ManageHeroState(
            isHeroLoading = false,
            isDeleteBottomSheetVisible = true
        ),
        onAction = {},
        snackBarHostState = SnackbarHostState()
    )
}

@Composable
private fun Screen(
    navBackStack: NavBackStack<NavKey>,
    state: ManageHeroState,
    onAction: (ManageHeroAction) -> Unit,
    snackBarHostState: SnackbarHostState
) {
    Scaffold(
        topBar = {
            CustomTopAppBar(
                navigationClick = { navBackStack.popBackStack() },
                title = if (state.isOnEdit) {
                    stringResource(R.string.action_edit)
                } else {
                    stringResource(R.string.action_add)
                }
            )
        },
        content = { innerPadding ->
            Content(
                modifier = Modifier
                    .padding(innerPadding),
                navBackStack = navBackStack,
                state = state,
                onAction = onAction
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackBarHostState) },
        floatingActionButton = {
            CustomSlideUpAnimatedVisibility(
                visible = !state.isContentLoading
            ) {
                CustomFloatingActionToolBar(
                    floatingActionButton = {
                        CustomFloatingActionButton(
                            onClick = { onAction(ManageHeroAction.UpsertButton) },
                            content = { Icon(Icons.Rounded.Save, null) },
                            isLoading = state.isUpsertButtonLoading
                        )
                    },
                    content = if (state.isOnEdit) {
                        {
                            CustomIconButton(
                                onClick = { onAction(ManageHeroAction.DeleteBottomSheet) },
                                content = { Icon(Icons.Rounded.Delete, null) },
                                isLoading = state.isDeleteButtonLoading
                            )
                        }
                    } else null
                )
            }
        }
    )

    CustomBottomSheet(
        visible = state.isDeleteBottomSheetVisible,
        onDismiss = { onAction(ManageHeroAction.DeleteBottomSheet) },
        title = stringResource(R.string.title_delete),
        content = {
            item {
                CustomSurfaceText(text = stringResource(R.string.message_delete_hero))
            }
        },
        bottomBar = {
            CustomButton(
                onClick = {
                    onAction(ManageHeroAction.DeleteBottomSheet)
                    onAction(ManageHeroAction.DeleteButton)
                },
                text = stringResource(R.string.action_confirm),
                isError = true
            )
        }
    )
}

@Composable
private fun Content(
    modifier: Modifier = Modifier,
    navBackStack: NavBackStack<NavKey>,
    state: ManageHeroState,
    onAction: (ManageHeroAction) -> Unit
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(start = 10.dp, end = 10.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(2.5.dp)
    ) {
        if (state.isContentLoading) {
            item("isContentLoading") {
                CustomCenterCircularWavyProgressIndicator(
                    modifier = Modifier
                        .animateItem()
                )
            }
            return@LazyColumn
        }
        item("heroTitle") {
            CustomTextListTitle(
                modifier = Modifier
                    .animateItem(),
                text = stringResource(R.string.item_hero)
            )
        }
        item("heroItem") {
            CustomTextField(
                modifier = Modifier
                    .animateItem(),
                label = stringResource(R.string.item_name),
                value = state.hero.name,
                onValueChange = { onAction(ManageHeroAction.HeroNameTextField(it)) }
            )
        }
    }
}