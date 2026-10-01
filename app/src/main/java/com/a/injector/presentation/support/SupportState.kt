package com.a.injector.presentation.support

import com.a.injector.domain.model.ProfileModel
import com.a.injector.domain.model.SupportModel
import com.a.injector.domain.model.Text
import io.github.vinceglb.filekit.PlatformFile

data class SupportState(
    val isProfileLoading: Boolean = true,
    val isProfileError: Text? = null,
    val profile: ProfileModel = ProfileModel.EMPTY,

    val isRequestedSupportLoading: Boolean = true,
    val isRequestedSupportError: Text? = null,
    val requestedSupport: SupportModel = SupportModel.EMPTY,

    val imageToUpload: PlatformFile? = null,

    val isUpsertSupportButtonLoading: Boolean = false
) {
    val isContentLoading: Boolean get() =
        isProfileLoading ||
        isRequestedSupportLoading

    val isRequested: Boolean get() = requestedSupport.id.isNotBlank()
}
