@file:OptIn(ExperimentalLayoutApi::class)

package com.a.injector.presentation.bottomnavigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.util.fastForEachIndexed
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack
import com.a.injector.presentation.account.AccountScreen
import com.a.injector.presentation.home.HomeScreenRoot
import com.a.injector.presentation.script.ScriptScreenRoot
import com.a.injector.presentation.settings.SettingsScreenRoot
import kotlinx.coroutines.launch

@Composable
fun BottomNavigationScreenRoot(
    navBackStack: NavBackStack<NavKey>
) {
    BottomNavigationScreen(
        navBackStack = navBackStack
    )
}

@Composable
@Preview
private fun Preview() {
    BottomNavigationScreen(
        navBackStack = rememberNavBackStack()
    )
}

@Composable
private fun BottomNavigationScreen(
    navBackStack: NavBackStack<NavKey>
) {
    val scope = rememberCoroutineScope()
    val isKeyboardVisible = WindowInsets.isImeVisible

    val pagerState = rememberPagerState(
        pageCount = { bottomNavigationItems.size }
    )
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        HorizontalPager(
            modifier = Modifier
                .weight(1f),
            state = pagerState,
        ) { pageContent ->
            when (pageContent) {
                0 -> HomeScreenRoot(navBackStack = navBackStack)
                1 -> ScriptScreenRoot(navBackStack = navBackStack)
                2 -> AccountScreen(navBackStack = navBackStack)
                3 -> SettingsScreenRoot(navBackStack = navBackStack)
            }
        }
        AnimatedVisibility(
            visible = !isKeyboardVisible
        ) {
            NavigationBar(
                content = {
                    bottomNavigationItems.fastForEachIndexed { index, static ->
                        NavigationBarItem(
                            selected = pagerState.currentPage == index,
                            onClick = {
                                scope.launch {
                                    pagerState.animateScrollToPage(index)
                                }
                            },
                            icon = { static.leadingContent?.invoke() },
                            label = { Text(text = stringResource(static.content)) }
                        )
                    }
                }
            )
        }
    }
}