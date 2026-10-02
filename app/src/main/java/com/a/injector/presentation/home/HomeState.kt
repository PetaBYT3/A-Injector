package com.a.injector.presentation.home

import com.a.injector.domain.model.InjectModel
import com.a.injector.domain.model.ProfileModel
import com.a.injector.domain.model.Text

data class HomeState(
    val isMaintenanceBottomSheetVisible: Boolean = false,
    val isUpdateBottomSheetVisible: Boolean = false,

    val isInjectLoading: Boolean = true,
    val inject: InjectModel = InjectModel.EMPTY,

    val isTopSupporterLoading: Boolean = true,
    val isTopSupporterError: Text? = null,
    val topSupporter: List<ProfileModel> = emptyList(),

    val isTopContributionLoading: Boolean = true,
    val isTopContributionError: Text? = null,
    val topContribution: List<ProfileModel> = emptyList()
)
