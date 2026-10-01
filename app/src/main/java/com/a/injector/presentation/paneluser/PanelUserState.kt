package com.a.injector.presentation.paneluser

import com.a.injector.domain.model.ProfileModel
import com.a.injector.domain.model.Text

data class PanelUserState(
    val isProfileLoading: Boolean = true,
    val isProfileError: Text? = null,
    val profiles: List<ProfileModel> = emptyList(),
    val filteredProfiles: List<ProfileModel> = emptyList(),

    val searchTextField: String = ""
) {
    val isContentLoading: Boolean get() =
        isProfileLoading
}
