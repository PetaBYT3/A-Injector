package com.a.injector.presentation.support

import com.a.injector.data.util.TextResource
import com.a.injector.domain.model.ProfileModel
import com.a.injector.domain.model.SupportModel

data class SupportState(
    val isProfileLoading: Boolean = true,
    val isProfileError: TextResource? = null,
    val profile: ProfileModel = ProfileModel.EMPTY,

    val isRequestedSupportLoading: Boolean = true,
    val isRequestedSupportError: TextResource? = null,
    val requestedSupport: SupportModel = SupportModel.EMPTY,

    val isUpsertSupportButtonLoading: Boolean = false
) {
    val isContentLoading: Boolean get() =
        isProfileLoading &&
        isRequestedSupportLoading

}
