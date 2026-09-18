package com.a.injector.presentation.script

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack
import com.a.injector.R
import com.a.injector.presentation.component.DefaultClickableListItem
import com.a.injector.presentation.component.ErrorListItem
import com.a.injector.presentation.navigation.NavigationRoute
import com.a.injector.presentation.util.CustomCenterCircularWavyProgressIndicator
import com.a.injector.presentation.util.CustomCenterTextMessage
import com.a.injector.presentation.util.CustomFloatingActionButton
import com.a.injector.presentation.util.CustomFloatingActionToolBar
import com.a.injector.presentation.util.CustomSlideUpAnimatedVisibility
import com.a.injector.presentation.util.CustomTopAppBar
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ScriptScreen(
    navBackStack: NavBackStack<NavKey>,
    viewModel: ScriptViewModel = koinViewModel()
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
        state = ScriptState(),
        onAction = {}
    )
}

@Composable
private fun Screen(
    navBackStack: NavBackStack<NavKey>,
    state: ScriptState,
    onAction: (ScriptAction) -> Unit
) {
    Scaffold(
        contentWindowInsets = WindowInsets.statusBars,
        topBar = {
            CustomTopAppBar(
                title = stringResource(R.string.script)
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
        floatingActionButton = {
            ScriptFloatingActionButton(
                navBackStack = navBackStack,
                state = state,
                onAction = onAction
            )
        }
    )
}

@Composable
private fun ScriptFloatingActionButton(
    navBackStack: NavBackStack<NavKey>,
    state: ScriptState,
    onAction: (ScriptAction) -> Unit
) {
    var isSearchExpand by rememberSaveable {
        mutableStateOf(false)
    }
    CustomSlideUpAnimatedVisibility(
        visible = !state.isContentLoading
    ) {
        CustomFloatingActionToolBar(
            floatingActionButton = if (state.isModifyEnabled) {
                {
                    CustomFloatingActionButton(
                        onClick = { navBackStack.add(NavigationRoute.ManageHeroScreen("")) },
                        content = { Icon(Icons.Rounded.Add, null) }
                    )
                }
            } else null,
            content = {
                AnimatedContent(
                    targetState = isSearchExpand
                ) { animatedContentState ->
                    if (animatedContentState) {
                        OutlinedTextField(
                            modifier = Modifier
                                .width(250.dp),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent
                            ),
                            placeholder = { Text(text = stringResource(R.string.script_search)) },
                            value = state.searchTextField,
                            onValueChange = { onAction(ScriptAction.SearchTextField(it)) },
                            trailingIcon = {
                                IconButton(
                                    onClick = {
                                        isSearchExpand = false
                                        onAction(ScriptAction.SearchTextField(""))
                                    },
                                    content = { Icon(Icons.Rounded.Close, null) }
                                )
                            }
                        )
                    } else {
                        IconButton(
                            onClick = { isSearchExpand = true },
                            content = { Icon(Icons.Rounded.Search, null) }
                        )
                    }
                }
            }
        )
    }
}

@Composable
private fun Content(
    modifier: Modifier = Modifier,
    navBackStack: NavBackStack<NavKey>,
    state: ScriptState,
    onAction: (ScriptAction) -> Unit
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(start = 10.dp, end = 10.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(2.5.dp)
    ) {
        if (state.isContentLoading) {
            item("isHeroesLoading") {
                CustomCenterCircularWavyProgressIndicator(
                    modifier = Modifier
                        .animateItem()
                )
            }
            return@LazyColumn
        }

        when {
            state.isHeroesError != null -> {
                item("isHeroesError") {
                    ErrorListItem(
                        modifier = Modifier
                            .animateItem(),
                        text = state.isHeroesError
                    )
                }
            }
            state.filteredHeroes.isEmpty() -> {
                item("isHeroesEmpty") {
                    CustomCenterTextMessage(
                        modifier = Modifier
                            .animateItem(),
                        text = stringResource(R.string.title_empty)
                    )
                }
            }
            else -> {
                itemsIndexed(
                    items = state.filteredHeroes,
                    key = { _, hero -> hero.id }
                ) { index, hero ->
                    DefaultClickableListItem(
                        modifier = Modifier
                            .animateItem(),
                        index = index,
                        count = state.filteredHeroes.size,
                        onClick = { navBackStack.add(NavigationRoute.HeroScreen(hero.id)) },
                        content = { Text(text = hero.name) },
                        trailingContent = {
                            if (state.isModifyEnabled) {
                                IconButton(
                                    onClick = {
                                        val targetRoute = NavigationRoute.ManageHeroScreen(
                                            heroId = hero.id
                                        )
                                        navBackStack.add(targetRoute)
                                    },
                                    content = { Icon(Icons.Rounded.Edit, null) }
                                )
                            }
                        }
                    )
                }
            }
        }
    }
}