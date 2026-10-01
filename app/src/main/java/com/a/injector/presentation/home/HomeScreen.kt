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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
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
import com.a.injector.presentation.component.CustomButton
import com.a.injector.presentation.component.CustomCenterCircularWavyProgressIndicator
import com.a.injector.presentation.component.CustomSurfaceText
import com.a.injector.presentation.component.CustomTextListTitle
import com.a.injector.presentation.component.CustomTopAppBar
import com.a.injector.presentation.component.DefaultClickableListItem
import com.a.injector.presentation.component.DefaultListItem
import com.a.injector.presentation.component.MessageListItem
import com.a.injector.presentation.component.PrimaryListItem
import com.a.injector.presentation.component.spacer
import com.a.injector.presentation.home.AboutDeveloper.Email
import com.a.injector.presentation.home.AboutDeveloper.Github
import com.a.injector.presentation.home.AboutDeveloper.Linkedin
import com.a.injector.presentation.home.AboutDeveloper.Mlbb
import com.a.injector.presentation.home.AboutDeveloper.Support
import com.a.injector.presentation.home.AboutDeveloper.Tiktok
import com.a.injector.presentation.mainnavigation.MainNavigationRoute
import com.a.injector.presentation.util.copyToClipboard
import com.a.injector.presentation.util.openInBrowser
import com.a.injector.presentation.util.openStoragePermissionSettings
import com.a.injector.presentation.util.sendToEmail
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
    val uriHandler = LocalUriHandler.current

    Scaffold(
        contentWindowInsets = WindowInsets.statusBars,
        topBar = {
            CustomTopAppBar(
                title = stringResource(R.string.home)
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
        }
    )

    CustomBottomSheet(
        visible = state.isMaintenanceBottomSheetVisible,
        onDismiss = { onAction(HomeAction.MaintenanceBottomSheet) },
        title = stringResource(R.string.maintenance),
        content = {
            item {
                CustomSurfaceText(text = stringResource(R.string.maintenance_desc))
            }
        },
        bottomBar = {
            CustomButton(
                onClick = { onAction(HomeAction.MaintenanceBottomSheet) },
                text = stringResource(R.string.dismiss)
            )
        }
    )

    CustomBottomSheet(
        visible = state.isUpdateBottomSheetVisible,
        onDismiss = { onAction(HomeAction.UpdateBottomSheet) },
        title = stringResource(R.string.update),
        content = {
            item {
                CustomSurfaceText(text = stringResource(R.string.update_desc))
            }
        },
        bottomBar = {
            CustomButton(
                onClick = {
                    onAction(HomeAction.UpdateBottomSheet)
                    openInBrowser(
                        uriHandler = uriHandler,
                        url = "https://github.com/PetaBYT3/A-Injector/releases",
                        onError = { onAction(HomeAction.ShowSnackBar(it)) }
                    )
                },
                text = stringResource(R.string.confirm)
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
    navBackStack: NavBackStack<NavKey>,
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
                content = { Text(text = stringResource(R.string.storage_permission)) },
                supportingContent = {
                    val text = when (state.isManageExternalStorageGranted) {
                        true -> stringResource(R.string.granted)
                        false -> stringResource(R.string.denied)
                    }
                    Text(text = text)
                },
                trailingContent = {
                    val context = LocalContext.current
                    if (!state.isManageExternalStorageGranted) {
                        Button(
                            onClick = { openStoragePermissionSettings(context) },
                            content = { Text(text = stringResource(R.string.settings)) }
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
                text = stringResource(R.string.about_developer)
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
                    if (staticModel.supportingContent != null) {
                        val supportingText = TextResource.StringResource(staticModel.supportingContent)
                        when (staticModel.id) {
                            Mlbb -> {
                                copyToClipboard(
                                    context = context,
                                    text = supportingText.asString(context)
                                )
                            }
                            Email -> {
                                sendToEmail(
                                    context = context,
                                    email = supportingText.asString(context),
                                    onError = { onAction(HomeAction.ShowSnackBar(it)) }
                                )
                            }
                            Linkedin, Tiktok, Github -> {
                                openInBrowser(
                                    uriHandler = uriHandler,
                                    url = supportingText.asString(context),
                                    onError = { onAction(HomeAction.ShowSnackBar(it)) }
                                )
                            }
                            Support -> {
                                navBackStack.add(MainNavigationRoute.SupportDevScreen)
                            }
                        }
                    }
                },
                leadingContent = staticModel.leadingContent,
                content = { Text(text = stringResource(staticModel.content)) },
                trailingContent = staticModel.trailingContent
            )
        }
        spacer()
        item("supporterTitle") {
            CustomTextListTitle(
                modifier = Modifier
                    .animateItem(),
                text = stringResource(R.string.top_support)
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
                        text = stringResource(R.string.empty)
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
                text = stringResource(R.string.top_contributor)
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
                        text = stringResource(R.string.empty)
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
                            val trailingText = buildString {
                                append(profileModel.contribution)
                                append(" ")
                                append(stringResource(R.string.top_contributor_desc))
                            }
                            Text(text = trailingText)
                        }
                    )
                }
            }
        }
    }
}