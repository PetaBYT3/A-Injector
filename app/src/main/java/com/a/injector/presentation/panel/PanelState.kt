package com.a.injector.presentation.panel

import com.a.injector.domain.model.ProfileModel
import com.a.injector.domain.model.RequestDetailModel

data class PanelState(
    val isCleanStorageBottomSheetVisible: Boolean = false,
    val isButtonCleanStorageLoading: Boolean = false,

    val isRequestDetailsLoading: Boolean = true,
    val isRequestDetailsError: String? = null,
    val requestDetails: List<RequestDetailModel> = emptyList(),

    val isGrantRequestBottomSheetVisible: Boolean = false,
    val requestToGrant: RequestDetailModel = RequestDetailModel.EMPTY,

    val isContributorProfilesLoading: Boolean = true,
    val isContributorProfilesError: String? = null,
    val contributorProfiles: List<ProfileModel> = emptyList(),

    val isDetachProfileBottomSheetVisible: Boolean = false,
    val profileToDetach: ProfileModel = ProfileModel.EMPTY
) {
    val isContentLoading: Boolean get() =
        isRequestDetailsLoading &&
        isContributorProfilesLoading
}
