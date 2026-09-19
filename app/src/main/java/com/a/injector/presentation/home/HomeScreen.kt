@file:OptIn(ExperimentalMaterial3Api::class)

package com.a.injector.presentation.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowDropDown
import androidx.compose.material.icons.rounded.OpenInNew
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEachIndexed
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack
import com.a.injector.R
import com.a.injector.data.local.CommandService
import com.a.injector.presentation.component.DefaultClickableListItem
import com.a.injector.presentation.component.DefaultListItem
import com.a.injector.presentation.component.ErrorListItem
import com.a.injector.presentation.component.PrimaryListItem
import com.a.injector.presentation.util.CustomCenterCircularWavyProgressIndicator
import com.a.injector.presentation.util.CustomCenterTextMessage
import com.a.injector.presentation.util.CustomTextListTitle
import com.a.injector.presentation.util.CustomTopAppBar
import com.a.injector.presentation.util.spacer
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
    Scaffold(
        contentWindowInsets = WindowInsets.statusBars,
        topBar = {
            CustomTopAppBar(
                title = stringResource(R.string.title_home)
            )
        },
        content = { innerPadding ->
            Content(
                modifier = Modifier
                    .padding(innerPadding),
                state = state,
                onAction = onAction
            )
        }
    )
}

@Composable
private fun Content(
    modifier: Modifier = Modifier,
    state: HomeState,
    onAction: (HomeAction) -> Unit
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(start = 10.dp, end = 10.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(2.5.dp)
    ) {
        item("serviceCommandItem") {
            var isCommandServiceExpanded by remember {
                mutableStateOf(false)
            }
            Column(
                modifier = Modifier
                    .animateItem(),
                verticalArrangement = Arrangement.spacedBy(2.5.dp)
            ) {
                PrimaryListItem(
                    overlineContent = { Text(text = stringResource(R.string.item_command_service)) },
                    content = { Text(text = state.commandService.name.name) },
                    supportingContent = {
                        val supportingText = when (state.commandService.name) {
                            CommandService.Shizuku -> {
                                if (state.commandService.isRunning) {
                                    stringResource(R.string.item_shizuku_true)
                                } else {
                                    stringResource(R.string.item_shizuku_false)
                                }
                            }
                            CommandService.Superuser -> {
                                if (state.commandService.isRunning) {
                                    stringResource(R.string.item_superuser_true)
                                } else {
                                    stringResource(R.string.item_superuser_false)
                                }
                            }
                        }
                        Text(text = supportingText)
                    },
                    trailingContent = {
                        val rotateIcon by animateFloatAsState(
                            targetValue = if (isCommandServiceExpanded) 180f else 0f
                        )
                        IconButton(
                            onClick = { isCommandServiceExpanded = !isCommandServiceExpanded },
                            content = {
                                Icon(
                                    modifier = Modifier
                                        .rotate(rotateIcon),
                                    imageVector = Icons.Rounded.ArrowDropDown,
                                    contentDescription = null
                                )
                            }
                        )
                    }
                )
                AnimatedVisibility(
                    visible = isCommandServiceExpanded
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(2.5.dp)
                    ) {
                        CommandService.entries.fastForEachIndexed { index, commandService ->
                            DefaultClickableListItem(
                                index = index,
                                count = CommandService.entries.size,
                                onClick = {
                                    onAction(HomeAction.SetCommandServiceButton(commandService))
                                },
                                leadingContent = {
                                    RadioButton(
                                        selected = state.commandService.name == commandService,
                                        onClick = null
                                    )
                                },
                                content = { Text(text = commandService.name) }
                            )
                        }
                    }
                }
            }
        }
        spacer()
        item("aboutDeveloperTitle") {
            CustomTextListTitle(
                modifier = Modifier
                    .animateItem(),
                text = stringResource(R.string.item_about_developer)
            )
        }
        itemsIndexed(
            items = homeAboutDeveloperItems,
            key = { _, staticModel -> staticModel.id.name }
        ) { index, staticModel ->
            DefaultClickableListItem(
                modifier = Modifier
                    .animateItem(),
                index = index,
                count = homeAboutDeveloperItems.size,
                onClick = {
                    when (staticModel.id) {
                        HomeAboutDeveloperId.Github -> {}
                        HomeAboutDeveloperId.Support -> {}
                    }
                },
                content = { Text(text = stringResource(staticModel.contentTextResId)) },
                trailingContent = { Icon(Icons.Rounded.OpenInNew, null) }
            )
        }
        spacer()
        item("contributorTitle") {
            CustomTextListTitle(
                modifier = Modifier
                    .animateItem(),
                text = stringResource(R.string.item_top_contributor)
            )
        }
        when {
            state.isHighestContributionProfileLoading -> {
                item("isHighestContributionProfileLoading") {
                    CustomCenterCircularWavyProgressIndicator(
                        modifier = Modifier
                            .animateItem()
                    )
                }
            }
            state.isHighestContributionProfileError != null -> {
                item("isHighestContributionProfileError") {
                    ErrorListItem(
                        modifier = Modifier
                            .animateItem(),
                        text = state.isHighestContributionProfileError
                    )
                }
            }
            state.highestContributionProfile.isEmpty() -> {
                item("isHighestContributionProfileEmpty") {
                    CustomCenterTextMessage(
                        modifier = Modifier
                            .animateItem(),
                        text = stringResource(R.string.item_empty)
                    )
                }
            }
            else -> {
                itemsIndexed(
                    items = state.highestContributionProfile,
                    key = { _, profileModel -> profileModel.id }
                ) { index, profileModel ->
                    DefaultListItem(
                        modifier = Modifier
                            .animateItem(),
                        index = index,
                        count = state.highestContributionProfile.size,
                        content = { Text(text = profileModel.username) },
                        supportingContent = {
                            Text(
                                text = "${profileModel.contribution} ${stringResource(R.string.item_files_uploaded)}"
                            )
                        }
                    )
                }
            }
        }
    }
}