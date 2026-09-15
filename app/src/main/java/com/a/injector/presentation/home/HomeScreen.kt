@file:OptIn(ExperimentalMaterial3Api::class)

package com.a.injector.presentation.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack
import com.a.injector.R
import com.a.injector.data.dto.Executor
import com.a.injector.presentation.util.CustomTopAppBar
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun HomeScreen(
    navBackStack: NavBackStack<NavKey>,
    viewModel: HomeViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val onAction = viewModel::onAction

    Screen(
        navBackStack = navBackStack,
        state = state,
        onAction = onAction
    )
}

@Composable
@Preview
private fun Preview() {
    Screen(
        navBackStack = rememberNavBackStack(),
        state = HomeState(),
        onAction = {}
    )
}

@Composable
private fun Screen(
    navBackStack: NavBackStack<NavKey>,
    state: HomeState,
    onAction: (HomeAction) -> Unit
) {
    val developerList = listOf(
        Pair(
            first = {},
            second = stringResource(R.string.developer_name)
        ),
        Pair(
            first = {},
            second = stringResource(R.string.github)
        ),
        Pair(
            first = {},
            second = stringResource(R.string.instagram)
        ),
        Pair(
            first = {},
            second = stringResource(R.string.title_support)
        )
    )

    Scaffold(
        contentWindowInsets = WindowInsets.statusBars,
        topBar = {
            CustomTopAppBar(
                title = stringResource(R.string.title_home)
            )
        },
        content = { innerPadding ->
            LazyColumn(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 10.dp),
                verticalArrangement = Arrangement.spacedBy(2.5.dp)
            ) {
                item {
                    SegmentedListItem(
                        colors = ListItemDefaults.colors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            leadingContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            supportingContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            trailingContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                        ),
                        shapes = ListItemDefaults.segmentedShapes(
                            index = 0,
                            count = Executor.entries.size
                        ),
                        onClick = { onAction(HomeAction.SetExecutor(Executor.Shizuku)) },
                        leadingContent = {
                            RadioButton(
                                selected = state.executor == Executor.Shizuku,
                                onClick = null
                            )
                        },
                        content = { Text(text = Executor.Shizuku.name) },
                        supportingContent = {
                            Text(
                                text = if (state.isAuthorized) stringResource(R.string.title_authorized)
                                else stringResource(R.string.title_unauthorized)
                            )
                        },
                        trailingContent = {
                            if (!state.isAuthorized) {
                                Icon(Icons.Rounded.Warning, null)
                            }
                        }
                    )
                }
                item {
                    SegmentedListItem(
                        colors = ListItemDefaults.colors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            leadingContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            supportingContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            trailingContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                        ),
                        shapes = ListItemDefaults.segmentedShapes(
                            index = 1,
                            count = Executor.entries.size
                        ),
                        onClick = { onAction(HomeAction.SetExecutor(Executor.Superuser)) },
                        leadingContent = {
                            RadioButton(
                                selected = state.executor == Executor.Superuser,
                                onClick = null
                            )
                        },
                        content = { Text(text = Executor.Superuser.name) },
                        supportingContent = {
                            Text(
                                text = if (state.isGranted) stringResource(R.string.state_granted)
                                else stringResource(R.string.title_denied)
                            )
                        },
                        trailingContent = {
                            if (!state.isGranted) {
                                Icon(Icons.Rounded.Warning, null)
                            }
                        }
                    )
                }
            }
        }
    )
}