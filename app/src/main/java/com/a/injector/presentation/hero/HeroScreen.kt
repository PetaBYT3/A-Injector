package com.a.injector.presentation.hero

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.icons.rounded.Person4
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import com.a.injector.domain.model.HeroDetailModel
import com.a.injector.presentation.component.DefaultClickableListItem
import com.a.injector.presentation.component.DefaultListItem
import com.a.injector.presentation.component.MessageListItem
import com.a.injector.presentation.component.PrimaryListItem
import com.a.injector.presentation.component.SkinDetailListItem
import com.a.injector.presentation.navigation.NavigationRoute
import com.a.injector.presentation.navigation.popBackStack
import com.a.injector.presentation.util.CustomBottomSheet
import com.a.injector.presentation.util.CustomCenterCircularWavyProgressIndicator
import com.a.injector.presentation.util.CustomCenterTextMessage
import com.a.injector.presentation.util.CustomFloatingActionButton
import com.a.injector.presentation.util.CustomFloatingActionToolBar
import com.a.injector.presentation.util.CustomIconButton
import com.a.injector.presentation.util.CustomSlideUpAnimatedVisibility
import com.a.injector.presentation.util.CustomTextListTitle
import com.a.injector.presentation.util.CustomTopAppBar
import com.a.injector.presentation.util.SnackBarEffectLauncher
import com.a.injector.presentation.util.spacer
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun HeroScreen(
    navBackStack: NavBackStack<NavKey>,
    heroId: String,
    viewModel: HeroViewModel = koinViewModel(
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
        state = HeroState(
            heroDetail = HeroDetailModel.EMPTY
        ),
        onAction = {},
        snackBarHostState = remember { SnackbarHostState() }
    )
}

@Composable
private fun Screen(
    navBackStack: NavBackStack<NavKey>,
    state: HeroState,
    onAction: (HeroAction) -> Unit,
    snackBarHostState: SnackbarHostState
) {
    Scaffold(
        topBar = {
            CustomTopAppBar(
                navigationClick = { navBackStack.popBackStack() },
                title = stringResource(R.string.title_hero)
            )
        },
        content = { innerPadding ->
            Content(
                innerPadding = innerPadding,
                navBackStack = navBackStack,
                state = state,
                onAction = onAction
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackBarHostState) },
        floatingActionButton = {
            if (state.isModifyEnabled) {
                CustomSlideUpAnimatedVisibility(
                    visible = !state.isContentLoading
                ) {
                    CustomFloatingActionToolBar(
                        floatingActionButton = {
                            CustomFloatingActionButton(
                                onClick = {
                                    val targetRoute = NavigationRoute.ManageSkinScreen(
                                        heroId = state.heroDetail.id,
                                        skinId = ""
                                    )
                                    navBackStack.add(targetRoute)
                                },
                                content = { Icon(Icons.Rounded.Add, null) }
                            )
                        }
                    )
                }
            }
        }
    )

    CustomBottomSheet(
        visible = state.isActionSkinBottomSheetVisible,
        onDismiss = { onAction(HeroAction.DismissSkinActionBottomSheet) },
        title = stringResource(R.string.title_action),
        content = {
            item {
                DefaultListItem(
                    overlineContent = { Text(text = state.skinToAction.label) },
                    content = { Text(text = state.skinToAction.name) }
                )
            }
            spacer()
            item {
                DefaultClickableListItem(
                    index = 0,
                    count = 2,
                    onClick = {
                        onAction(HeroAction.DismissSkinActionBottomSheet)
                        val targetRoute = NavigationRoute.ManageSkinScreen(
                            heroId = state.heroDetail.id,
                            skinId = state.skinToAction.id
                        )
                        navBackStack.add(targetRoute)
                    },
                    leadingContent = { Icon(Icons.Rounded.Edit, null) },
                    content = { Text(text = stringResource(R.string.title_update)) }
                )
            }
            item {
                DefaultClickableListItem(
                    index = 1,
                    count = 2,
                    onClick = {
                        onAction(HeroAction.DismissSkinActionBottomSheet)
                        val targetRoute = NavigationRoute.ManageReplaceScreen(
                            heroId = state.heroDetail.id,
                            skinId = state.skinToAction.id,
                            replaceId = ""
                        )
                        navBackStack.add(targetRoute)
                    },
                    leadingContent = { Icon(Icons.Rounded.Add, null) },
                    content = { Text(text = "${stringResource(R.string.title_add)} ${stringResource(R.string.title_replace)}") }
                )
            }
        }
    )
}

@Composable
private fun Content(
    innerPadding: PaddingValues,
    navBackStack: NavBackStack<NavKey>,
    state: HeroState,
    onAction: (HeroAction) -> Unit
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

        if (state.isHeroDetailError != null) {
            item("isHeroDetailError") {
                MessageListItem(
                    modifier = Modifier
                        .animateItem(),
                    text = state.isHeroDetailError,
                    isError = true
                )
            }
            return@LazyColumn
        }

        item("heroItem") {
            PrimaryListItem(
                modifier = Modifier
                    .animateItem(),
                leadingContent = { Icon(Icons.Rounded.Person4, null) },
                content = {
                    Text(
                        text = state.heroDetail.name,
                        style = MaterialTheme.typography.displaySmall
                    )
                }
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
        if (state.heroDetail.skins.isEmpty()) {
            item("isSkinWithReplaceEmpty") {
                CustomCenterTextMessage(
                    modifier = Modifier
                        .animateItem(),
                    text = stringResource(R.string.title_empty)
                )
            }
            return@LazyColumn
        }
        itemsIndexed(
            items = state.heroDetail.skins,
            key = { _, skin -> skin.id }
        ) { index, skin ->
            SkinDetailListItem(
                modifier = Modifier
                    .animateItem(),
                skinDetail = skin,
                skinTrailingContent = {
                    if (state.isModifyEnabled) {
                        IconButton(
                            onClick = {
                                onAction(HeroAction.ShowActionSkinBottomSheet(skin))
                            },
                            content = { Icon(Icons.Rounded.MoreVert, null) }
                        )
                    }
                },
                replaceTrailingContent = { replace ->
                    val isFileExist = replace.lastUpdate != null || replace.fileSize != null
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        if (state.isModifyEnabled) {
                            IconButton(
                                onClick = {
                                    val targetRoute = NavigationRoute.ManageReplaceScreen(
                                        heroId = state.heroDetail.id,
                                        skinId = skin.id,
                                        replaceId = replace.id
                                    )
                                    navBackStack.add(targetRoute)
                                },
                                content = { Icon(Icons.Rounded.Edit, null) }
                            )
                        }
                        if (isFileExist) {
                            CustomIconButton(
                                onClick = { onAction(HeroAction.StartInject(replace)) },
                                content = { Icon(Icons.Rounded.Download, null) },
                                isLoading = state.isInjectLoading[replace.id] != null
                            )
                        }
                    }
                }
            )
        }
    }
}