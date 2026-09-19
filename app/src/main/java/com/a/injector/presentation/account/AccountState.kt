package com.a.injector.presentation.account

import com.a.injector.domain.model.ProfileModel
import com.a.injector.domain.model.state.RequestState
import io.github.jan.supabase.auth.user.UserInfo

data class AccountState(
    val isUserInfoLoading: Boolean = true,
    val userInfo: UserInfo? = null,

    val isProfileLoading: Boolean = true,
    val profile: ProfileModel = ProfileModel.EMPTY,
    val requestState: RequestState = RequestState.NotApplied,

    val isUpsertProfileBottomSheetVisible: Boolean = false,
    val profileToUpsert: ProfileModel = ProfileModel.EMPTY,
    val isUpsertProfileButtonLoading: Boolean = false,

    val isSignOutBottomSheetVisible: Boolean = false,
    val isSingOutButtonLoading: Boolean = false
) {
    val isContentLoading: Boolean get() =
        isUserInfoLoading &&
        isProfileLoading
}
