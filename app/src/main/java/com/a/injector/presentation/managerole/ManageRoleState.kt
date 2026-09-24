package com.a.injector.presentation.managerole

import com.a.injector.data.util.TextResource
import com.a.injector.domain.model.ProfileModel
import com.a.injector.domain.model.RequestDetailModel

data class ManageRoleState(
    val isRequestDetailsLoading: Boolean = true,
    val isRequestDetailsError: TextResource? = null,
    val requestDetails: List<RequestDetailModel> = emptyList(),

    val isGrantRequestBottomSheetVisible: Boolean = false,
    val requestToGrant: RequestDetailModel = RequestDetailModel.EMPTY,

    val isContributorProfilesLoading: Boolean = true,
    val isContributorProfilesError: TextResource? = null,
    val contributorProfiles: List<ProfileModel> = emptyList(),

    val isDetachProfileBottomSheetVisible: Boolean = false,
    val profileToDetach: ProfileModel = ProfileModel.EMPTY
) {
    val isContentLoading: Boolean get() =
        isRequestDetailsLoading &&
        isContributorProfilesLoading
}
