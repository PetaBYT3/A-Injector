package com.a.injector.presentation.managehero

import com.a.injector.data.util.TextResource
import com.a.injector.domain.model.HeroModel

data class ManageHeroState(
    val isOnEdit: Boolean = false,

    val isHeroLoading: Boolean = true,
    val isHeroError: TextResource? = null,
    val hero: HeroModel = HeroModel.EMPTY,

    val isDeleteBottomSheetVisible: Boolean = false,
    val isDeleteButtonLoading: Boolean = false,

    val isUpsertButtonLoading: Boolean = false
) {
    val isContentLoading: Boolean get() = isHeroLoading
}