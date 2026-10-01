package com.a.injector.presentation.managehero

import com.a.injector.domain.model.HeroModel
import com.a.injector.domain.model.Text

data class ManageHeroState(
    val isOnEdit: Boolean = false,

    val isProfileLoading: Boolean = true,
    val isDeleteEnabled: Boolean = false,

    val isHeroLoading: Boolean = true,
    val isHeroError: Text? = null,
    val hero: HeroModel = HeroModel.EMPTY,

    val isDeleteBottomSheetVisible: Boolean = false,
    val isDeleteButtonLoading: Boolean = false,

    val isUpsertButtonLoading: Boolean = false
) {
    val isContentLoading: Boolean get() =
        isProfileLoading ||
        isHeroLoading
}