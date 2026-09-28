package com.a.injector.presentation.supporting

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack
import com.a.injector.R
import com.a.injector.presentation.component.CustomFloatingActionButton
import com.a.injector.presentation.component.CustomFloatingActionToolBar
import com.a.injector.presentation.component.CustomIconButton
import com.a.injector.presentation.component.CustomTextField
import com.a.injector.presentation.component.CustomTopAppBar
import com.a.injector.presentation.component.DefaultClickableListItem
import com.a.injector.presentation.component.spacer
import com.a.injector.presentation.mainnavigation.popBackStack
import com.a.injector.presentation.util.ScreenEffectLauncher
import io.github.vinceglb.filekit.dialogs.FileKitMode
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.compose.rememberFilePickerLauncher
import io.github.vinceglb.filekit.name
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun SupportingScreenRoot(
    navBackStack: NavBackStack<NavKey>,
    profileId: String,
    viewModel: SupportingViewModel = koinViewModel(
        parameters = {
            parametersOf(profileId)
        }
    )
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
        state = SupportingState(),
        onAction = {},
        snackBarHostState = SnackbarHostState()
    )
}

@Composable
private fun SupportingScreen(
    navBackStack: NavBackStack<NavKey>,
    state: SupportingState,
    onAction: (SupportingAction) -> Unit,
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
            CustomFloatingActionToolBar(
                floatingActionButton = {
                    CustomFloatingActionButton(
                        onClick = { onAction(SupportingAction.UpsertSupportingButton) },
                        content = { Icon(Icons.Rounded.OpenInNew, null) },
                        isLoading = state.isUpsertSupportingButtonLoading
                    )
                }
            )
        }
    )
}

@Composable
private fun Content(
    modifier: Modifier = Modifier,
    navBackStack: NavBackStack<NavKey>,
    state: SupportingState,
    onAction: (SupportingAction) -> Unit,
) {
    val filePicker = rememberFilePickerLauncher(
        type = FileKitType.Image,
        mode = FileKitMode.Single,
        onResult = { platformFile ->
            if (platformFile != null) {
                onAction(SupportingAction.ImagePicker(platformFile))
            }
        }
    )

    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(start = 10.dp, end = 10.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(2.5.dp)
    ) {
        item("nominalTextField") {
            CustomTextField(
                label = stringResource(R.string.item_support_nominal),
                value = state.nominalTextField,
                onValueChange = { userInput ->
                    val filteredInput = userInput.filter { it.isDigit() }
                    onAction(SupportingAction.NominalTextField(filteredInput))
                }
            )
        }
        spacer()
        item("image") {
            DefaultClickableListItem(
                onClick = {

                },
                content = { Text(text = "Proof") },
                supportingContent = {
                    val supportingText = if (state.image != null) {
                        state.image.name
                    } else {
                        stringResource(R.string.exception_no_image)
                    }
                    Text(text = supportingText)
                },
                trailingContent = {
                    CustomIconButton(
                        onClick = { filePicker.launch() },
                        content = { Icon(Icons.Rounded.AttachFile, null) }
                    )
                }
            )
        }
    }
}