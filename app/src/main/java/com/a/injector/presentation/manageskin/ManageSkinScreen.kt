package com.a.injector.presentation.manageskin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Person4
import androidx.compose.material.icons.rounded.Save
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
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
import com.a.injector.presentation.component.DefaultListItem
import com.a.injector.presentation.navigation.popBackStack
import com.a.injector.presentation.util.CustomBottomSheet
import com.a.injector.presentation.util.CustomButton
import com.a.injector.presentation.util.CustomCenterCircularWavyProgressIndicator
import com.a.injector.presentation.util.CustomFloatingActionButton
import com.a.injector.presentation.util.CustomFloatingActionToolBar
import com.a.injector.presentation.util.CustomIconButton
import com.a.injector.presentation.util.CustomSlideUpAnimatedVisibility
import com.a.injector.presentation.util.CustomSurfaceText
import com.a.injector.presentation.util.CustomTextField
import com.a.injector.presentation.util.CustomTextListTitle
import com.a.injector.presentation.util.CustomTopAppBar
import com.a.injector.presentation.util.SnackBarEffectLauncher
import com.a.injector.presentation.util.spacer
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun ManageSkinScreen(
    navBackStack: NavBackStack<NavKey>,
    heroId: String,
    skinId: String,
    viewModel: ManageSkinViewModel = koinViewModel(
        parameters = {
            parametersOf(heroId, skinId)
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

    SnackBarEffectLauncher(
        snackBarHostState = snackBarHostState,
        screenEffect = viewModel.effect
    )
}

@Composable
@Preview
private fun Preview() {
    Screen(
        navBackStack = rememberNavBackStack(),
        state = ManageSkinState(
            isHeroLoading = false,
            isSkinLoading = false
        ),
        onAction = {  },
        snackBarHostState = SnackbarHostState()
    )
}

@Composable
private fun Screen(
    navBackStack: NavBackStack<NavKey>,
    state: ManageSkinState,
    onAction: (ManageSkinAction) -> Unit,
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
                },
            )
        },
        content = { innerPadding ->
            Content(
                innerPadding = innerPadding,
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
                            onClick = { onAction(ManageSkinAction.UpsertButton) },
                            content = { Icon(Icons.Rounded.Save, null) },
                            isLoading = state.isUpsertButtonLoading
                        )
                    },
                    content = if (state.isOnEdit) {
                        {
                            CustomIconButton(
                                onClick = { onAction(ManageSkinAction.DeleteBottomSheet) },
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
        onDismiss = { onAction(ManageSkinAction.DeleteBottomSheet) },
        title = stringResource(R.string.action_delete),
        content = {
            item {
                CustomSurfaceText(text = stringResource(R.string.message_delete_skin))
            }
        },
        bottomBar = {
            CustomButton(
                onClick = {
                    onAction(ManageSkinAction.DeleteBottomSheet)
                    onAction(ManageSkinAction.DeleteButton)
                },
                text = stringResource(R.string.action_confirm),
                isError = true
            )
        }
    )
}

@Composable
private fun Content(
    innerPadding: PaddingValues,
    state: ManageSkinState,
    onAction: (ManageSkinAction) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding),
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

        item("heroItem") {
            DefaultListItem(
                modifier = Modifier
                    .animateItem(),
                leadingContent = { Icon(Icons.Rounded.Person4, null) },
                content = { Text(text = state.hero.name) }
            )
        }
        spacer()
        item("skinTitle") {
            CustomTextListTitle(
                modifier = Modifier
                    .animateItem(),
                text = stringResource(R.string.item_skin)
            )
        }
        item("skinItem") {
            Column(
                modifier = Modifier
                    .animateItem(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                CustomTextField(
                    label = stringResource(R.string.item_label),
                    value = state.skin.label,
                    onValueChange = { onAction(ManageSkinAction.SkinLabelTextField(it)) }
                )
                CustomTextField(
                    label = stringResource(R.string.item_name),
                    value = state.skin.name,
                    onValueChange = { onAction(ManageSkinAction.SkinNameTextField(it)) }
                )
            }
        }
    }
}