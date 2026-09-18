package com.a.injector.presentation.bottomnavigation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.InsertDriveFile
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.util.fastForEachIndexed
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack
import com.a.injector.R
import com.a.injector.presentation.account.AccountScreen
import com.a.injector.presentation.home.HomeScreen
import com.a.injector.presentation.script.ScriptScreen
import kotlinx.coroutines.launch

@Composable
fun BottomNavigation(
    navBackStack: NavBackStack<NavKey>
) {
    val scope = rememberCoroutineScope()

    val bottomNavigationItems = listOf(
        Pair(
            first = Icons.Rounded.Home,
            second = R.string.title_home
        ),
        Pair(
            first = Icons.Rounded.InsertDriveFile,
            second = R.string.title_script
        ),
        Pair(
            first = Icons.Rounded.Person,
            second = stringResource(R.string.title_profile)
        )
    )

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
                0 -> {
                    HomeScreen(
                        navBackStack = navBackStack
                    )
                }
                1 -> {
                    ScriptScreen(
                        navBackStack = navBackStack
                    )
                }
                2 -> {
                    AccountScreen(
                        navBackStack = navBackStack
                    )
                }
            }
        }
        NavigationBar(
            content = {
                bottomNavigationItems.fastForEachIndexed { index, pair ->
                    NavigationBarItem(
                        selected = pagerState.currentPage == index,
                        onClick = {
                            scope.launch {
                                pagerState.animateScrollToPage(index)
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = pair.first,
                                contentDescription = null
                            )
                        }
                    )
                }
            }
        )
    }
}

@Composable
@Preview
private fun Preview() {
    BottomNavigation(
        navBackStack = rememberNavBackStack()
    )
}