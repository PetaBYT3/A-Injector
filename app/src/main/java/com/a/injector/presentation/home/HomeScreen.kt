@file:OptIn(ExperimentalMaterial3Api::class)

package com.a.injector.presentation.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.OpenInNew
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Storage
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack
import com.a.injector.R
import com.a.injector.presentation.component.CustomCenterCircularWavyProgressIndicator
import com.a.injector.presentation.component.CustomTextListTitle
import com.a.injector.presentation.component.CustomTopAppBar
import com.a.injector.presentation.component.DefaultClickableListItem
import com.a.injector.presentation.component.DefaultListItem
import com.a.injector.presentation.component.MessageListItem
import com.a.injector.presentation.component.PrimaryListItem
import com.a.injector.presentation.component.spacer
import com.a.injector.presentation.util.openInBrowser
import com.a.injector.presentation.util.openStoragePermissionSettings
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
        state = HomeState(
            isManageExternalStorageGranted = false
        ),
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
    val context = LocalContext.current
    val uriHandler = LocalUriHandler.current

    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(start = 10.dp, end = 10.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(2.5.dp)
    ) {
        item("methodItem") {
            PrimaryListItem(
                modifier = Modifier
                    .animateItem(),
                leadingContent = { Icon(Icons.Rounded.Storage, null) },
                content = { Text(text = stringResource(R.string.item_storage_permission)) },
                supportingContent = {
                    Text(
                        text = if (state.isManageExternalStorageGranted) {
                            stringResource(R.string.title_granted)
                        } else {
                            stringResource(R.string.title_denied)
                        }
                    )
                },
                trailingContent = {
                    if (!state.isManageExternalStorageGranted) {
                        Button(
                            onClick = { openStoragePermissionSettings(context) },
                            content = { Text(text = stringResource(R.string.item_settings)) }
                        )
                    }
                }
            )
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
                    val url = when (staticModel.id) {
                        HomeAboutDeveloperId.Github -> "https://github.com/PetaBYT3"
                        HomeAboutDeveloperId.Support -> ""
                    }
                    openInBrowser(
                        context = context,
                        uriHandler = uriHandler,
                        url = url
                    )
                },
                leadingContent = {
                    val imageVector = when (staticModel.id) {
                        HomeAboutDeveloperId.Github -> ImageVector.vectorResource(R.drawable.github)
                        HomeAboutDeveloperId.Support -> ImageVector.vectorResource(R.drawable.support)
                    }
                    Icon(imageVector, null)
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
                    MessageListItem(
                        modifier = Modifier
                            .animateItem(),
                        text = state.isHighestContributionProfileError.asString(),
                        isError = true
                    )
                }
            }
            state.highestContributionProfile.isEmpty() -> {
                item("isHighestContributionProfileEmpty") {
                    MessageListItem(
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
                        leadingContent = { Icon(Icons.Rounded.Person, null) },
                        content = {
                            Text(
                                text = profileModel.username,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        },
                        trailingContent = {
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