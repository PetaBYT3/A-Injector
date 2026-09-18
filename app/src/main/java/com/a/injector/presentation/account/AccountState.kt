package com.a.injector.presentation.account

import com.a.injector.R
import com.a.injector.domain.model.ProfileModel
import com.a.injector.domain.model.state.RequestState
import io.github.jan.supabase.auth.user.UserInfo

data class AccountState(
    val isGuestAccount: Boolean = false,

    val isProfileLoading: Boolean = true,
    val userInfo: UserInfo? = null,
    val profile: ProfileModel = ProfileModel.EMPTY,
    val requestState: RequestState = RequestState.NotApplied,

    val isSignOutBottomSheetVisible: Boolean = false,
    val isSingOutButtonLoading: Boolean = false
) {
    val isContentLoading: Boolean get() =
        isProfileLoading

    val profiles: List<Pair<Int, String>> = listOf(
        Pair(R.string.title_email, profile.username),
        Pair(R.string.title_role, profile.role.name),
        Pair(R.string.title_contribution, profile.contribution.toString())
    )
}
