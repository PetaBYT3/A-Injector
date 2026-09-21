package com.a.injector.presentation.home

import com.a.injector.domain.model.ProfileModel

data class HomeState(
    val isManageExternalStorageGranted: Boolean = false,

    val isHighestContributionProfileLoading: Boolean = true,
    val isHighestContributionProfileError: String? = null,
    val highestContributionProfile: List<ProfileModel> = emptyList()
)
