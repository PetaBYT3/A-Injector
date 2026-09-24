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
import androidx.compose.material.icons.rounded.Person4
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack
import com.a.injector.R
import com.a.injector.domain.model.HeroModel
import com.a.injector.presentation.component.CustomCenterCircularWavyProgressIndicator
import com.a.injector.presentation.component.CustomFloatingActionButton
import com.a.injector.presentation.component.CustomFloatingActionToolBar
import com.a.injector.presentation.component.CustomSlideUpAnimatedVisibility
import com.a.injector.presentation.component.CustomTopAppBar
import com.a.injector.presentation.component.DefaultClickableListItem
import com.a.injector.presentation.component.MessageListItem
import com.a.injector.presentation.component.TransparentTextField
import com.a.injector.presentation.navigation.NavigationRoute
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
        state = ScriptState(
            isHeroesLoading = false,
            filteredHeroes = List(10) {
                HeroModel("", "Hero Preview")
            }
        ),
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
                title = stringResource(R.string.title_script)
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
                        TransparentTextField(
                            modifier = Modifier
                                .width(250.dp),
                            placeholder = stringResource(R.string.action_search),
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
                    MessageListItem(
                        modifier = Modifier
                            .animateItem(),
                        text = state.isHeroesError.asString(),
                        isError = true
                    )
                }
            }
            state.filteredHeroes.isEmpty() -> {
                item("isHeroesEmpty") {
                    MessageListItem(
                        modifier = Modifier
                            .animateItem(),
                        text = stringResource(R.string.item_empty)
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
                        leadingContent = { Icon(Icons.Rounded.Person4, null) },
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