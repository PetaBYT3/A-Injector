package com.a.injector.presentation.navigation

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.imePadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.a.injector.domain.model.state.NavigationState
import com.a.injector.domain.repository.NavigationRepository
import com.a.injector.presentation.bottomnavigation.BottomNavigation
import com.a.injector.presentation.hero.HeroScreen
import com.a.injector.presentation.landing.LandingScreen
import com.a.injector.presentation.loading.LoadingScreen
import com.a.injector.presentation.managehero.ManageHeroScreen
import com.a.injector.presentation.managereplace.ManageReplaceScreen
import com.a.injector.presentation.manageskin.ManageSkinScreen
import com.a.injector.presentation.panel.PanelScreen
import com.a.injector.presentation.signin.SignInScreen
import com.a.injector.presentation.signup.SignUpScreen
import org.koin.compose.koinInject

@Composable
fun NavigationScreen(
    navigationRepository: NavigationRepository = koinInject()
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val navBackStack = rememberNavBackStack(NavigationRoute.LoadingScreen)

    LaunchedEffect(lifecycleOwner.lifecycle, navigationRepository.navigationEffect) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
            navigationRepository.navigationEffect.collect { navigationState ->
                when (navigationState) {
                    is NavigationState.NavigateTo -> {
                        navBackStack.add(navigationState.route)
                    }
                    is NavigationState.ReplaceTo -> {
                        navBackStack.clear()
                        navBackStack.add(navigationState.route)
                    }
                    NavigationState.PopBackStack -> {
                        navBackStack.popBackStack()
                    }
                }
            }
        }
    }

    val transitionDuration = 250
    NavDisplay(
        modifier = Modifier
            .imePadding(),
        backStack = navBackStack,
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        ),
        entryProvider = { navKey ->
            when (navKey) {
                is NavigationRoute.LoadingScreen -> {
                    NavEntry(navKey) {
                        LoadingScreen(
                            navBackStack = navBackStack
                        )
                    }
                }
                is NavigationRoute.LandingScreen -> {
                    NavEntry(navKey) {
                        LandingScreen(
                            navBackStack = navBackStack
                        )
                    }
                }
                is NavigationRoute.SignInScreen -> {
                    NavEntry(navKey) {
                        SignInScreen(
                            navBackStack = navBackStack
                        )
                    }
                }
                is NavigationRoute.SignUpScreen -> {
                    NavEntry(navKey) {
                        SignUpScreen(
                            navBackStack = navBackStack
                        )
                    }
                }
                is NavigationRoute.BottomNavigation -> {
                    NavEntry(navKey) {
                        BottomNavigation(
                            navBackStack = navBackStack
                        )
                    }
                }
                is NavigationRoute.HeroScreen -> {
                    NavEntry(navKey) {
                        HeroScreen(
                            navBackStack = navBackStack,
                            heroId = navKey.heroId,
                        )
                    }
                }
                is NavigationRoute.ManageHeroScreen -> {
                    NavEntry(navKey) {
                        ManageHeroScreen(
                            navBackStack = navBackStack,
                            heroId = navKey.heroId
                        )
                    }
                }
                is NavigationRoute.ManageSkinScreen -> {
                    NavEntry(navKey) {
                        ManageSkinScreen(
                            navBackStack = navBackStack,
                            heroId = navKey.heroId,
                            skinId = navKey.skinId
                        )
                    }
                }
                is NavigationRoute.ManageReplaceScreen -> {
                    NavEntry(navKey) {
                        ManageReplaceScreen(
                            navBackStack = navBackStack,
                            heroId = navKey.heroId,
                            skinId = navKey.skinId,
                            replaceId = navKey.replaceId
                        )
                    }
                }
                is NavigationRoute.ManageRoleScreen -> {
                    NavEntry(navKey) {
                        PanelScreen(
                            navBackStack = navBackStack
                        )
                    }
                }
                else -> error("Unknown NavKey: $navKey")
            }
        },
        transitionSpec = {
            slideInHorizontally(
                initialOffsetX = { it },
                animationSpec = tween(transitionDuration, easing = FastOutSlowInEasing)
            ) togetherWith slideOutHorizontally(
                targetOffsetX = { -it / 3 },
                animationSpec = tween(transitionDuration, easing = FastOutSlowInEasing)
            )
        },
        popTransitionSpec = {
            slideInHorizontally(
                initialOffsetX = { -it / 3 },
                animationSpec = tween(transitionDuration, easing = FastOutSlowInEasing)
            ) togetherWith slideOutHorizontally(
                targetOffsetX = { it },
                animationSpec = tween(transitionDuration, easing = FastOutSlowInEasing)
            )
        },
        predictivePopTransitionSpec = {
            slideInHorizontally(
                initialOffsetX = { -it / 3 },
                animationSpec = tween(transitionDuration, easing = FastOutSlowInEasing)
            ) togetherWith slideOutHorizontally(
                targetOffsetX = { it },
                animationSpec = tween(transitionDuration, easing = FastOutSlowInEasing)
            )
        }
    )
}

fun NavBackStack<NavKey>.popBackStack() {
    this.removeAt(this.lastIndex)
}