package com.a.injector.presentation.home

import com.a.injector.domain.model.CommandServiceModel
import com.a.injector.domain.model.ProfileModel

data class HomeState(
    val commandService: CommandServiceModel = CommandServiceModel.DEFAULT,

    val isHighestContributionProfileLoading: Boolean = true,
    val isHighestContributionProfileError: String? = null,
    val highestContributionProfile: List<ProfileModel> = emptyList()
)
