package com.a.injector.presentation.managereplace

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AttachFile
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.InsertDriveFile
import androidx.compose.material.icons.rounded.Person4
import androidx.compose.material.icons.rounded.Save
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
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
import com.a.injector.presentation.component.CustomHorizontalToolBar
import com.a.injector.presentation.component.CustomIconButton
import com.a.injector.presentation.component.CustomSlideUpAnimatedVisibility
import com.a.injector.presentation.component.CustomSurfaceText
import com.a.injector.presentation.component.CustomTextField
import com.a.injector.presentation.component.CustomTopAppBar
import com.a.injector.presentation.component.DefaultListItem
import com.a.injector.presentation.component.spacer
import com.a.injector.presentation.mainnavigation.popBackStack
import com.a.injector.presentation.util.ScreenEffectLauncher
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.compose.PickerResultLauncher
import io.github.vinceglb.filekit.dialogs.compose.rememberFilePickerLauncher
import io.github.vinceglb.filekit.name
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun ManageReplaceScreenRoot(
    navBackStack: NavBackStack<NavKey>,
    heroId: String,
    skinId: String,
    replaceId: String,
    viewModel: ManageReplaceViewModel = koinViewModel(
        parameters = {
            parametersOf(heroId, skinId, replaceId)
        }
    )
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackBarHostState = remember { SnackbarHostState() }

    ManageReplaceScreen(
        navBackStack = navBackStack,
        state = state,
        onAction = viewModel::onAction,
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
    ManageReplaceScreen(
        navBackStack = rememberNavBackStack(),
        state = ManageReplaceState(
            isProfileLoading = false,
            isHeroLoading = false,
            isSkinLoading = false,
            isReplaceLoading = false
        ),
        onAction = {},
        snackBarHostState = SnackbarHostState()
    )
}

@Composable
private fun ManageReplaceScreen(
    navBackStack: NavBackStack<NavKey>,
    state: ManageReplaceState,
    onAction: (ManageReplaceAction) -> Unit,
    snackBarHostState: SnackbarHostState
) {
    val filePicker = rememberFilePickerLauncher(
        type = FileKitType.File(extension = "zip"),
        onResult = { platformFile ->
            if (platformFile != null) {
                onAction(ManageReplaceAction.ReplaceFilePicker(platformFile))
            }
        }
    )

    Scaffold(
        topBar = {
            val title = when (state.isOnEdit) {
                true -> stringResource(R.string.edit)
                false -> stringResource(R.string.add)
            }
            CustomTopAppBar(
                navigationClick = { navBackStack.popBackStack() },
                title = title
            )
        },
        content = { innerPadding ->
            Content(
                modifier = Modifier
                    .padding(innerPadding),
                navBackStack = navBackStack,
                filePicker = filePicker,
                state = state,
                onAction = onAction
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackBarHostState) },
        floatingActionButton = {
            CustomSlideUpAnimatedVisibility(
                visible = !state.isContentLoading
            ) {
                CustomHorizontalToolBar(
                    floatingActionButton = {
                        CustomFloatingActionButton(
                            onClick = { onAction(ManageReplaceAction.UpsertButton) },
                            content = Icons.Rounded.Save,
                            isLoading = state.isUpsertButtonLoading
                        )
                    },
                    content = if (state.isOnEdit && state.isDeleteEnabled) {
                        {
                            CustomIconButton(
                                onClick = { onAction(ManageReplaceAction.DeleteBottomSheet) },
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
        onDismiss = { onAction(ManageReplaceAction.DeleteBottomSheet) },
        title = stringResource(R.string.delete),
        content = {
            item {
                CustomSurfaceText(text = stringResource(R.string.replace_delete))
            }
        },
        bottomBar = {
            CustomButton(
                onClick = {
                    onAction(ManageReplaceAction.DeleteBottomSheet)
                    onAction(ManageReplaceAction.DeleteButton)
                },
                text = stringResource(R.string.confirm),
                isError = true
            )
        }
    )
}

@Composable
private fun Content(
    modifier: Modifier = Modifier,
    navBackStack: NavBackStack<NavKey>,
    filePicker: PickerResultLauncher,
    state: ManageReplaceState,
    onAction: (ManageReplaceAction) -> Unit
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
        item("heroItem") {
            DefaultListItem(
                modifier = Modifier
                    .animateItem(),
                index = 0,
                count = 2,
                leadingContent = { Icon(Icons.Rounded.Person4, null) },
                content = { Text(text = state.hero.name) }
            )
        }
        item("skinItem") {
            DefaultListItem(
                modifier = Modifier
                    .animateItem(),
                index = 1,
                count = 2,
                leadingContent = { Icon(ImageVector.vectorResource(R.drawable.skin), null) },
                content = { Text(text = state.skin.label) },
                supportingContent = { Text(text = state.skin.name) }
            )
        }
        spacer()
        item("replaceTitle") {
            DefaultListItem(
                modifier = Modifier
                    .animateItem(),
                index = 0,
                count = 4,
                content = { Text(text = stringResource(R.string.replace)) }
            )
        }
        item("labelTextField") {
            CustomTextField(
                label = stringResource(R.string.label),
                value = state.replace.label,
                onValueChange = { onAction(ManageReplaceAction.LabelTextField(it)) }
            )
        }
        item("nameTextField") {
            CustomTextField(
                label = stringResource(R.string.name),
                value = state.replace.name,
                onValueChange = { onAction(ManageReplaceAction.NameTextField(it)) }
            )
        }
        item("scriptFile") {
            DefaultListItem(
                index = 3,
                count = 4,
                leadingContent = { Icon(Icons.Rounded.InsertDriveFile, null) },
                content = { Text(text = stringResource(R.string.script)) },
                supportingContent = {
                    Text(
                        text = state.replaceFile?.name ?: stringResource(R.string.exception_no_script)
                    )
                },
                trailingContent = {
                    IconButton(
                        onClick = {
                            if (state.replaceFile != null) {
                                onAction(ManageReplaceAction.ReplaceFilePicker(null))
                            } else {
                                filePicker.launch()
                            }
                        },
                        content = {
                            val imageVector = if (state.replaceFile != null) {
                                Icons.Rounded.Delete
                            } else {
                                Icons.Rounded.AttachFile
                            }
                            Icon(imageVector, null)
                        }
                    )
                }
            )
        }
    }
}