package com.a.injector.presentation.paneluser

import com.a.injector.data.util.TextResource
import com.a.injector.domain.model.ProfileModel

data class PanelUserState(
    val isProfileLoading: Boolean = true,
    val isProfileError: TextResource? = null,
    val profiles: List<ProfileModel> = emptyList(),
    val filteredProfiles: List<ProfileModel> = emptyList(),

    val searchTextField: String = ""
) {
    val isContentLoading: Boolean get() =
        isProfileLoading
}
