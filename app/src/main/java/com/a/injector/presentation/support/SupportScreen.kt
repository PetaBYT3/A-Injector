package com.a.injector.presentation.support

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AttachFile
import androidx.compose.material.icons.rounded.OpenInNew
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack
import com.a.injector.R
import com.a.injector.presentation.component.CustomCenterCircularWavyProgressIndicator
import com.a.injector.presentation.component.CustomCenterTextMessage
import com.a.injector.presentation.component.CustomFloatingActionButton
import com.a.injector.presentation.component.CustomFloatingActionToolBar
import com.a.injector.presentation.component.CustomIconButton
import com.a.injector.presentation.component.CustomSlideUpAnimatedVisibility
import com.a.injector.presentation.component.CustomTextField
import com.a.injector.presentation.component.CustomTopAppBar
import com.a.injector.presentation.component.DefaultClickableListItem
import com.a.injector.presentation.component.DefaultListItem
import com.a.injector.presentation.component.spacer
import com.a.injector.presentation.mainnavigation.MainNavigationRoute
import com.a.injector.presentation.mainnavigation.popBackStack
import com.a.injector.presentation.util.IdrVisualTransformation
import com.a.injector.presentation.util.ScreenEffectLauncher
import com.a.injector.presentation.util.toIdr
import io.github.vinceglb.filekit.dialogs.FileKitMode
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.compose.rememberFilePickerLauncher
import io.github.vinceglb.filekit.name
import io.github.vinceglb.filekit.path
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SupportingScreenRoot(
    navBackStack: NavBackStack<NavKey>,
    viewModel: SupportViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackBarHostState = remember { SnackbarHostState() }

    SupportingScreen(
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
    SupportingScreen(
        navBackStack = rememberNavBackStack(),
        state = SupportState(
            isProfileLoading = false,
            isRequestedSupportLoading = false
        ),
        onAction = {},
        snackBarHostState = SnackbarHostState()
    )
}

@Composable
private fun SupportingScreen(
    navBackStack: NavBackStack<NavKey>,
    state: SupportState,
    onAction: (SupportAction) -> Unit,
    snackBarHostState: SnackbarHostState
) {
    Scaffold(
        topBar = {
            CustomTopAppBar(
                navigationClick = { navBackStack.popBackStack() },
                title = stringResource(R.string.support)
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
            if (!state.isRequested) {
                CustomSlideUpAnimatedVisibility(
                    visible = !state.isContentLoading && !state.isRequested
                ) {
                    CustomFloatingActionToolBar(
                        floatingActionButton = {
                            CustomFloatingActionButton(
                                onClick = { onAction(SupportAction.UpsertSupportingButton) },
                                content = { Icon(Icons.Rounded.OpenInNew, null) },
                                isLoading = state.isUpsertSupportButtonLoading
                            )
                        }
                    )
                }
            }
        }
    )
}

@Composable
private fun Content(
    modifier: Modifier = Modifier,
    navBackStack: NavBackStack<NavKey>,
    state: SupportState,
    onAction: (SupportAction) -> Unit,
) {
    val filePicker = rememberFilePickerLauncher(
        type = FileKitType.Image,
        mode = FileKitMode.Single,
        onResult = { platformFile ->
            if (platformFile != null) {
                onAction(SupportAction.ImagePicker(platformFile))
            }
        }
    )

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
        if (state.isProfileError != null) {
            item("isProfileError") {
                CustomCenterTextMessage(
                    modifier = Modifier
                        .animateItem(),
                    text = state.isProfileError.asString()
                )
            }
        }
        item("currentTitle") {
            DefaultListItem(
                modifier = Modifier
                    .animateItem(),
                content = { Text(text = stringResource(R.string.support_cur)) },
                supportingContent = { Text(text = state.profile.nominal.toIdr()) }
            )
        }
        spacer()
        item("requestedTitle") {
            DefaultListItem(
                index = 0,
                count = 3,
                content = { Text(text = stringResource(R.string.support_req)) },
                trailingContent = {
                    if (state.isRequested) {
                        Text(text = stringResource(R.string.waiting))
                    }
                }
            )
        }
        item("requestedNominal") {
            CustomTextField(
                label = stringResource(R.string.nominal),
                value = state.requestedSupport.nominal.toString(),
                onValueChange = { userInput ->
                    val filteredInput = userInput.filter { it.isDigit() }
                    onAction(SupportAction.NominalTextField(filteredInput))
                },
                visualTransformation = IdrVisualTransformation(""),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                ),
                readOnly = state.isRequested
            )
        }
        item("requestedProof") {
            DefaultClickableListItem(
                index = 2,
                count = 3,
                content = { Text(text = stringResource(R.string.proof)) },
                onClick = {
                    val imageToView = state.requestedSupport.imageUrl.takeIf { imageUrl ->
                        imageUrl.isNotBlank()
                    } ?: state.imageToUpload?.path

                    if (imageToView != null) {
                        navBackStack.add(MainNavigationRoute.ImagePreviewScreen(imageToView))
                    }
                },
                supportingContent = {
                    val supportingText = state.requestedSupport.imageUrl.takeIf { imageUrl ->
                        imageUrl.isNotBlank()
                    } ?: state.imageToUpload?.name ?: stringResource(R.string.exception_no_image)
                    Text(
                        text = supportingText,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                trailingContent = {
                    CustomIconButton(
                        onClick = { if (!state.isRequested) filePicker.launch() },
                        content = { Icon(Icons.Rounded.AttachFile, null) }
                    )
                }
            )
        }
    }
}