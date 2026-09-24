package com.a.injector.presentation.home

import com.a.injector.data.util.TextResource
import com.a.injector.domain.model.ProfileModel

data class HomeState(
    val isManageExternalStorageGranted: Boolean = false,

    val isHighestContributionProfileLoading: Boolean = true,
    val isHighestContributionProfileError: TextResource? = null,
    val highestContributionProfile: List<ProfileModel> = emptyList()
)
