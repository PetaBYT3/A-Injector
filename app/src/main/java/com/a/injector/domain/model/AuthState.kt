package com.a.injector.domain.model

import io.github.jan.supabase.auth.user.UserInfo

sealed interface AuthState {
    data object Loading: AuthState
    data object Unauthorized: AuthState
    data object EmailNotVerified: AuthState
    data class Authorized(val userInfo: UserInfo, val profileModel: ProfileModel): AuthState
    data object Guest: AuthState
}