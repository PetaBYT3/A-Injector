package com.a.injector.presentation.mainnavigation

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
import com.a.injector.presentation.bottomnavigation.BottomNavigationScreenRoot
import com.a.injector.presentation.hero.HeroScreenRoot
import com.a.injector.presentation.landing.LandingScreenRoot
import com.a.injector.presentation.loading.LoadingScreenRoot
import com.a.injector.presentation.managehero.ManageHeroScreenRoot
import com.a.injector.presentation.managereplace.ManageReplaceScreenRoot
import com.a.injector.presentation.managerole.ManageRoleScreenRoot
import com.a.injector.presentation.manageskin.ManageSkinScreen
import com.a.injector.presentation.signin.SignInScreenRoot
import com.a.injector.presentation.signlink.SignLinkScreenRoot
import com.a.injector.presentation.signup.SignUpScreenRoot
import org.koin.compose.koinInject

@Composable
fun NavigationScreen(
    navigationRepository: NavigationRepository = koinInject()
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val navBackStack = rememberNavBackStack(MainNavigationRoute.LoadingScreen())

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
                is MainNavigationRoute.LoadingScreen -> {
                    NavEntry(navKey) {
                        LoadingScreenRoot(
                            navBackStack = navBackStack,
                            signMethod = navKey.signMethod
                        )
                    }
                }
                is MainNavigationRoute.LandingScreen -> {
                    NavEntry(navKey) {
                        LandingScreenRoot(
                            navBackStack = navBackStack
                        )
                    }
                }
                is MainNavigationRoute.SignInScreen -> {
                    NavEntry(navKey) {
                        SignInScreenRoot(
                            navBackStack = navBackStack
                        )
                    }
                }
                is MainNavigationRoute.SignUpScreen -> {
                    NavEntry(navKey) {
                        SignUpScreenRoot(
                            navBackStack = navBackStack
                        )
                    }
                }
                is MainNavigationRoute.SignLinkScreen -> {
                    NavEntry(navKey) {
                        SignLinkScreenRoot(
                            navBackStack = navBackStack
                        )
                    }
                }
                is MainNavigationRoute.BottomNavigation -> {
                    NavEntry(navKey) {
                        BottomNavigationScreenRoot(
                            navBackStack = navBackStack
                        )
                    }
                }
                is MainNavigationRoute.HeroScreen -> {
                    NavEntry(navKey) {
                        HeroScreenRoot(
                            navBackStack = navBackStack,
                            heroId = navKey.heroId,
                        )
                    }
                }
                is MainNavigationRoute.ManageHeroScreen -> {
                    NavEntry(navKey) {
                        ManageHeroScreenRoot(
                            navBackStack = navBackStack,
                            heroId = navKey.heroId
                        )
                    }
                }
                is MainNavigationRoute.ManageSkinScreen -> {
                    NavEntry(navKey) {
                        ManageSkinScreen(
                            navBackStack = navBackStack,
                            heroId = navKey.heroId,
                            skinId = navKey.skinId
                        )
                    }
                }
                is MainNavigationRoute.ManageReplaceScreen -> {
                    NavEntry(navKey) {
                        ManageReplaceScreenRoot(
                            navBackStack = navBackStack,
                            heroId = navKey.heroId,
                            skinId = navKey.skinId,
                            replaceId = navKey.replaceId
                        )
                    }
                }
                is MainNavigationRoute.ManageRoleScreen -> {
                    NavEntry(navKey) {
                        ManageRoleScreenRoot(
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