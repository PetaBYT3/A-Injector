package com.a.injector.presentation.paneluser

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack
import com.a.injector.R
import com.a.injector.presentation.component.CustomCenterCircularWavyProgressIndicator
import com.a.injector.presentation.component.CustomFloatingActionToolBar
import com.a.injector.presentation.component.CustomIconButton
import com.a.injector.presentation.component.CustomTopAppBar
import com.a.injector.presentation.component.DefaultListItem
import com.a.injector.presentation.component.MessageListItem
import com.a.injector.presentation.component.TransparentTextField
import com.a.injector.presentation.mainnavigation.MainNavigationRoute
import com.a.injector.presentation.mainnavigation.popBackStack
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun PanelUserScreenRoot(
    navBackStack: NavBackStack<NavKey>,
    viewModel: PanelUserViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackBarHostState = remember { SnackbarHostState() }

    PanelUserScreen(
        navBackStack = navBackStack,
        state = state,
        onAction = viewModel::onAction,
        snackBarHostState = snackBarHostState
    )
}

@Composable
@Preview
private fun Preview() {
    PanelUserScreen(
        navBackStack = rememberNavBackStack(),
        state = PanelUserState(),
        onAction = {},
        snackBarHostState = SnackbarHostState()
    )
}

@Composable
private fun PanelUserScreen(
    navBackStack: NavBackStack<NavKey>,
    state: PanelUserState,
    onAction: (PanelUserAction) -> Unit,
    snackBarHostState: SnackbarHostState
) {
    var isSearchExpanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CustomTopAppBar(
                navigationClick = { navBackStack.popBackStack() },
                title = stringResource(R.string.user_panel)
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
            CustomFloatingActionToolBar {
                AnimatedContent(
                    targetState = isSearchExpanded
                ) { animatedContentState ->
                    if (animatedContentState) {
                        TransparentTextField(
                            modifier = Modifier
                                .width(250.dp),
                            placeholder = stringResource(R.string.search_here),
                            value = state.searchTextField,
                            onValueChange = { onAction(PanelUserAction.SearchTextField(it)) },
                            trailingIcon = {
                                IconButton(
                                    onClick = {
                                        isSearchExpanded = false
                                        onAction(PanelUserAction.SearchTextField(""))
                                    },
                                    content = { Icon(Icons.Rounded.Close, null) }
                                )
                            }
                        )
                    } else {
                        IconButton(
                            onClick = { isSearchExpanded = true },
                            content = { Icon(Icons.Rounded.Search, null) }
                        )
                    }
                }
            }
        }
    )
}

@Composable
private fun Content(
    modifier: Modifier = Modifier,
    navBackStack: NavBackStack<NavKey>,
    state: PanelUserState,
    onAction: (PanelUserAction) -> Unit
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
        if (state.isProfileError != null) {
            item("isProfileError") {
                MessageListItem(
                    modifier = Modifier
                        .animateItem(),
                    text = state.isProfileError.asString()
                )
            }
            return@LazyColumn
        }
        if (state.filteredProfiles.isEmpty()) {
            item("isProfileEmpty") {
                MessageListItem(
                    modifier = Modifier
                        .animateItem(),
                    text = stringResource(R.string.empty)
                )
            }
            return@LazyColumn
        }
        itemsIndexed(
            items = state.filteredProfiles,
            key = { _, profileModel -> profileModel.id }
        ) { index, profileModel ->
            DefaultListItem(
                modifier = Modifier
                    .animateItem(),
                index = index,
                count = state.filteredProfiles.size,
                content = {
                    Text(
                        text = profileModel.username,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                supportingContent = { Text(text = profileModel.role.name) },
                trailingContent = {
                    CustomIconButton(
                        onClick = {
                            navBackStack.add(MainNavigationRoute.ManageUserScreen(profileModel.id))
                        },
                        content = { Icon(Icons.Rounded.Edit, null) }
                    )
                }
            )
        }
    }
}