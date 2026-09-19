package com.a.injector.presentation.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed interface NavigationRoute: NavKey {
    @Serializable
    data object LoadingScreen: NavigationRoute, NavKey

    @Serializable
    data object LandingScreen: NavigationRoute, NavKey

    @Serializable
    data object SignInScreen: NavigationRoute, NavKey

    @Serializable
    data object SignUpScreen: NavigationRoute, NavKey

    @Serializable
    data object VerifyEmailScreen: NavigationRoute, NavKey

    @Serializable
    data object BottomNavigation: NavigationRoute, NavKey

    @Serializable
    data class HeroScreen(val heroId: String): NavigationRoute, NavKey

    @Serializable
    data class ManageHeroScreen(val heroId: String): NavigationRoute, NavKey

    @Serializable
    data class ManageSkinScreen(val heroId: String, val skinId: String): NavigationRoute, NavKey

    @Serializable
    data class ManageReplaceScreen(val heroId: String, val skinId: String, val replaceId: String): NavigationRoute, NavKey

    @Serializable
    data object ManageRoleScreen: NavigationRoute, NavKey
}