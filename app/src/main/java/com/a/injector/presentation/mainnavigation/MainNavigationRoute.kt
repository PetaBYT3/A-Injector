package com.a.injector.presentation.mainnavigation

import androidx.navigation3.runtime.NavKey
import com.a.injector.presentation.loading.SignMethod
import kotlinx.serialization.Serializable

@Serializable
sealed interface MainNavigationRoute: NavKey {
    @Serializable
    data class LoadingScreen(val signMethod: SignMethod = SignMethod.EmailPassword): MainNavigationRoute, NavKey

    @Serializable
    data object LandingScreen: MainNavigationRoute, NavKey

    @Serializable
    data object SignInScreen: MainNavigationRoute, NavKey

    @Serializable
    data object SignUpScreen: MainNavigationRoute, NavKey

    @Serializable
    data object SignLinkScreen: MainNavigationRoute, NavKey

    @Serializable
    data object BottomNavigation: MainNavigationRoute, NavKey

    @Serializable
    data class HeroScreen(val heroId: String): MainNavigationRoute, NavKey

    @Serializable
    data class ManageHeroScreen(val heroId: String): MainNavigationRoute, NavKey

    @Serializable
    data class ManageSkinScreen(val heroId: String, val skinId: String): MainNavigationRoute, NavKey

    @Serializable
    data class ManageReplaceScreen(val heroId: String, val skinId: String, val replaceId: String): MainNavigationRoute, NavKey

    @Serializable
    data object ManageRoleScreen: MainNavigationRoute, NavKey
}