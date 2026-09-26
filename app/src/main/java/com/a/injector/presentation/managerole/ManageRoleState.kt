package com.a.injector.presentation.managerole

import com.a.injector.data.util.TextResource
import com.a.injector.domain.model.RequestModel

data class ManageRoleState(
    val isRequestDetailsLoading: Boolean = true,
    val isRequestDetailsError: TextResource? = null,
    val requestDetails: List<RequestModel> = emptyList(),

    val isGrantRequestBottomSheetVisible: Boolean = false,
    val requestToGrant: RequestModel = RequestModel.EMPTY,
) {
    val isContentLoading: Boolean get() =
        isRequestDetailsLoading
}
