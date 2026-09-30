package com.a.injector.presentation.home

import com.a.injector.data.util.TextResource
import com.a.injector.domain.model.ProfileModel

data class HomeState(
    val isMaintenanceBottomSheetVisible: Boolean = false,
    val isUpdateBottomSheetVisible: Boolean = false,

    val isManageExternalStorageGranted: Boolean = false,
    val isSupportBottomSheetVisible: Boolean = false,

    val isTopSupporterLoading: Boolean = true,
    val isTopSupporterError: TextResource? = null,
    val topSupporter: List<ProfileModel> = emptyList(),

    val isTopContributionLoading: Boolean = true,
    val isTopContributionError: TextResource? = null,
    val topContribution: List<ProfileModel> = emptyList()
)
