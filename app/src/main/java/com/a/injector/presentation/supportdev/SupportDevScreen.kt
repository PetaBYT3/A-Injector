package com.a.injector.presentation.supportdev

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.AbsoluteRoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack
import com.a.injector.R
import com.a.injector.presentation.component.CustomBottomSheet
import com.a.injector.presentation.component.CustomTopAppBar
import com.a.injector.presentation.component.DefaultClickableListItem
import com.a.injector.presentation.component.DefaultListItem
import com.a.injector.presentation.component.spacer
import com.a.injector.presentation.mainnavigation.popBackStack
import com.a.injector.presentation.supportdev.SupportMethod.Qris
import com.a.injector.presentation.supportdev.SupportMethod.Seabank
import com.a.injector.presentation.util.copyToClipboard
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SupportDevScreenRoot(
    navBackStack: NavBackStack<NavKey>,
    viewModel: SupportDevViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    SupportDevScreen(
        navBackStack = navBackStack,
        state = state,
        onAction = viewModel::onAction
    )
}

@Composable
@Preview
private fun Preview() {
    SupportDevScreen(
        navBackStack = rememberNavBackStack(),
        state = SupportDevState(),
        onAction = {}
    )
}

@Composable
private fun SupportDevScreen(
    navBackStack: NavBackStack<NavKey>,
    state: SupportDevState,
    onAction: (SupportDevAction) -> Unit
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
                state = state,
                onAction = onAction
            )
        }
    )

    CustomBottomSheet(
        visible = state.isQrisBottomSheetVisible,
        onDismiss = { onAction(SupportDevAction.QrisBottomSheet) },
        title = stringResource(R.string.qris),
        content = {
            item {
                DefaultListItem(
                    index = 0,
                    count = 2,
                    content = { Text(text = stringResource(R.string.qris_desc)) },
                    supportingContent = { Text(text = stringResource(R.string.qris_meta)) }
                )
            }
            item {
                DefaultListItem(
                    index = 1,
                    count = 2,
                    content = {
                        Image(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(AbsoluteRoundedCornerShape(bottomLeft = 16.dp, bottomRight = 16.dp)),
                            painter = painterResource(R.drawable.qris),
                            contentDescription = null
                        )
                    }
                )
            }
        }
    )
}

@Composable
private fun Content(
    modifier: Modifier = Modifier,
    state: SupportDevState,
    onAction: (SupportDevAction) -> Unit
) {
    val context = LocalContext.current

    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(start = 10.dp, end = 10.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(2.5.dp)
    ) {
        item("supportMethodTitle") {
            DefaultListItem(
                modifier = Modifier
                    .animateItem(),
                index = 0,
                count = supportMethods.size + 1,
                content = { Text(text = stringResource(R.string.support_method)) }
            )
        }
        itemsIndexed(
            items = supportMethods,
            key = { _, static -> static.id.name }
        ) { index, static ->
            val supportingText = static.supportingContent?.let { stringResource(it) } ?: ""
            DefaultClickableListItem(
                modifier = Modifier
                    .animateItem(),
                index = index + 1,
                count = supportMethods.size + 1,
                onClick = {
                    when (static.id) {
                        Qris -> {
                            onAction(SupportDevAction.QrisBottomSheet)
                        }
                        Seabank -> {
                            copyToClipboard(
                                context = context,
                                text = supportingText
                            )
                        }
                    }
                },
                leadingContent = static.leadingContent,
                content = { Text(text = stringResource(static.content)) },
                supportingContent = if (static.supportingContent != null) {
                    { Text(text = stringResource(static.supportingContent)) }
                } else null
            )
        }
        spacer()
        item("validateSupportStepTitle") {
            DefaultListItem(
                modifier = Modifier
                    .animateItem(),
                index = 0,
                count = validateSupportSteps.size + 1,
                content = { Text(text = stringResource(R.string.validate_support_step)) }
            )
        }
        itemsIndexed(
            items = validateSupportSteps,
            key = { _, stringResource -> stringResource }
        ) { index, stringResource ->
            DefaultListItem(
                modifier = Modifier
                    .animateItem(),
                index = index + 1,
                count = validateSupportSteps.size + 1,
                leadingContent = { Text(text = (index + 1).toString()) },
                content = {
                    Text(
                        text = stringResource(stringResource),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            )
        }
    }
}