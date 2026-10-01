package com.a.injector.presentation.account

import com.a.injector.data.util.TextResource
import com.a.injector.domain.model.ProfileModel
import com.a.injector.domain.model.state.AuthResult
import io.github.jan.supabase.auth.user.UserInfo

data class AccountState(
    val isAuthStateLoading: Boolean = true,
    val authState: AuthResult = AuthResult.Unauthenticated,

    val isUserInfoLoading: Boolean = true,
    val isUserInfoError: TextResource? = null,
    val userInfo: UserInfo? = null,

    val isProfileLoading: Boolean = true,
    val isProfileError: TextResource? = null,
    val profile: ProfileModel = ProfileModel.EMPTY,

    val isCleanStorageBottomSheetVisible: Boolean = false,
    val isCleanStorageButtonLoading: Boolean = false,

    val isSignOutBottomSheetVisible: Boolean = false,
    val isSingOutButtonLoading: Boolean = false
) {
    val isContentLoading: Boolean get() =
        isAuthStateLoading ||
        isUserInfoLoading ||
        isProfileLoading
}
