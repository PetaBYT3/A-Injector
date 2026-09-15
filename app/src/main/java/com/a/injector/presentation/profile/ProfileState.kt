package com.a.injector.presentation.profile

import com.a.injector.R
import com.a.injector.domain.model.ProfileModel
import io.github.jan.supabase.auth.user.UserInfo

data class ProfileState(
    val isProfileLoading: Boolean = true,
    val userInfo: UserInfo? = null,
    val profile: ProfileModel = ProfileModel.EMPTY
) {
    val isContentLoading: Boolean get() =
        isProfileLoading

    val profiles: List<Pair<Int, String>> = listOf(
        Pair(R.string.title_email, profile.email),
        Pair(R.string.title_role, profile.role.name),
        Pair(R.string.title_contribution, profile.contribution.toString())
    )
}
