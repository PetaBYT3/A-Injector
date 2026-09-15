package com.a.injector.presentation.managerole

import com.a.injector.domain.model.ProfileModel
import com.a.injector.domain.model.RequestDetailModel
import com.a.injector.domain.model.RequestModel

data class ManageRoleState(
    val isRequestDetailsLoading: Boolean = true,
    val isRequestDetailsError: String? = null,
    val requestDetails: List<RequestDetailModel> = emptyList(),

    val isGrantRequestBottomSheetVisible: Boolean = false,
    val requestToGrant: RequestModel = RequestModel.EMPTY,

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
