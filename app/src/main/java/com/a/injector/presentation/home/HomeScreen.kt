@file:OptIn(ExperimentalMaterial3Api::class)

package com.a.injector.presentation.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Storage
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack
import com.a.injector.R
import com.a.injector.data.util.TextResource
import com.a.injector.presentation.component.CustomBottomSheet
import com.a.injector.presentation.component.CustomCenterCircularWavyProgressIndicator
import com.a.injector.presentation.component.CustomTextListTitle
import com.a.injector.presentation.component.CustomTopAppBar
import com.a.injector.presentation.component.DefaultClickableListItem
import com.a.injector.presentation.component.DefaultListItem
import com.a.injector.presentation.component.MessageListItem
import com.a.injector.presentation.component.PrimaryListItem
import com.a.injector.presentation.component.spacer
import com.a.injector.presentation.home.AboutDeveloper.Github
import com.a.injector.presentation.home.AboutDeveloper.Linkedin
import com.a.injector.presentation.home.AboutDeveloper.Mlbb
import com.a.injector.presentation.home.AboutDeveloper.Support
import com.a.injector.presentation.home.AboutDeveloper.Tiktok
import com.a.injector.presentation.util.openInBrowser
import com.a.injector.presentation.util.openStoragePermissionSettings
import com.a.injector.presentation.util.toIdr
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun HomeScreenRoot(
    navBackStack: NavBackStack<NavKey>,
    viewModel: HomeViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val onAction = viewModel::onAction

    HomeScreen(
        navBackStack = navBackStack,
        state = state,
        onAction = onAction
    )
}

@Composable
@Preview
private fun Preview() {
    HomeScreen(
        navBackStack = rememberNavBackStack(),
        state = HomeState(
            isManageExternalStorageGranted = false
        ),
        onAction = {}
    )
}

@Composable
private fun HomeScreen(
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

    CustomBottomSheet(
        visible = state.isSupportBottomSheetVisible,
        onDismiss = { onAction(HomeAction.SupportBottomSheet) },
        title = stringResource(R.string.support),
        content = {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        modifier = Modifier
                            .fillMaxWidth(0.65f)
                            .aspectRatio(1f),
                        painter = painterResource(R.drawable.qris),
                        contentDescription = null
                    )
                }
            }
            spacer()
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
    val clipboardManager = LocalClipboardManager.current

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
                    val text = when (state.isManageExternalStorageGranted) {
                        true -> stringResource(R.string.title_granted)
                        false -> stringResource(R.string.title_denied)
                    }
                    Text(text = text)
                },
                trailingContent = {
                    val context = LocalContext.current
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
            items = AboutDevelopers,
            key = { _, staticModel -> staticModel.id.name }
        ) { index, staticModel ->
            DefaultClickableListItem(
                modifier = Modifier
                    .animateItem(),
                index = index,
                count = AboutDevelopers.size,
                onClick = {
                    if (staticModel.supportingTextResId != null) {
                        val supportingText = TextResource.StringResource(staticModel.supportingTextResId)
                        when (staticModel.id) {
                            Mlbb -> {
                                clipboardManager.setText(
                                    annotatedString = AnnotatedString(
                                        text = supportingText.asString(context)
                                    )
                                )
                            }
                            Linkedin, Tiktok, Github -> {
                                openInBrowser(
                                    context = context,
                                    uriHandler = uriHandler,
                                    url = supportingText.asString(context)
                                )
                            }
                            Support -> {
                                onAction(HomeAction.SupportBottomSheet)
                            }
                        }
                    }
                },
                leadingContent = staticModel.leadingContent,
                content = { Text(text = stringResource(staticModel.contentTextResId)) },
                trailingContent = staticModel.trailingContent
            )
        }
        spacer()
        item("supporterTitle") {
            CustomTextListTitle(
                modifier = Modifier
                    .animateItem(),
                text = stringResource(R.string.item_top_supporter)
            )
        }
        when {
            state.isTopSupporterLoading -> {
                item("isTopSupporterLoading") {
                    CustomCenterCircularWavyProgressIndicator(
                        modifier = Modifier
                            .animateItem()
                    )
                }
            }
            state.isTopSupporterError != null -> {
                item("isTopSupporterError") {
                    MessageListItem(
                        modifier = Modifier
                            .animateItem(),
                        text = state.isTopSupporterError.asString()
                    )
                }
            }
            state.topSupporter.isEmpty() -> {
                item("isTopSupporterEmpty") {
                    MessageListItem(
                        modifier = Modifier
                            .animateItem(),
                        text = stringResource(R.string.item_empty)
                    )
                }
            }
            else -> {
                itemsIndexed(
                    items = state.topSupporter,
                    key = { _, profileModel -> "topSupporter${profileModel.id}" }
                ) { index, profileModel ->
                    DefaultListItem(
                        modifier = Modifier
                            .animateItem(),
                        index = index,
                        count = state.topSupporter.size,
                        leadingContent = { Icon(Icons.Rounded.Person, null) },
                        content = {
                            Text(
                                text = profileModel.username,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        },
                        trailingContent = {
                            Text(text = profileModel.nominal.toIdr())
                        }
                    )
                }
            }
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
            state.isTopContributionLoading -> {
                item("isHighestContributionProfileLoading") {
                    CustomCenterCircularWavyProgressIndicator(
                        modifier = Modifier
                            .animateItem()
                    )
                }
            }
            state.isTopContributionError != null -> {
                item("isHighestContributionProfileError") {
                    MessageListItem(
                        modifier = Modifier
                            .animateItem(),
                        text = state.isTopContributionError.asString(),
                        isError = true
                    )
                }
            }
            state.topContribution.isEmpty() -> {
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
                    items = state.topContribution,
                    key = { _, profileModel -> "topContributor${profileModel.id}" }
                ) { index, profileModel ->
                    DefaultListItem(
                        modifier = Modifier
                            .animateItem(),
                        index = index,
                        count = state.topContribution.size,
                        leadingContent = { Icon(Icons.Rounded.Person, null) },
                        content = {
                            Text(
                                text = profileModel.username,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        },
                        trailingContent = {
                            val text = "${profileModel.contribution} ${stringResource(R.string.item_files_uploaded)}"
                            Text(text = text)
                        }
                    )
                }
            }
        }
    }
}