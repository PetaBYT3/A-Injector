package com.a.injector.presentation.manageskin

import com.a.injector.domain.model.HeroModel
import com.a.injector.domain.model.SkinModel

data class ManageSkinState(
    val isOnEdit: Boolean = false,

    val isHeroLoading: Boolean = true,
    val isHeroError: String? = null,
    val hero: HeroModel = HeroModel.EMPTY,

    val isSkinLoading: Boolean = true,
    val isSkinError: String? = null,
    val skin: SkinModel = SkinModel.EMPTY,

    val isDeleteBottomSheetVisible: Boolean = false,
    val isDeleteButtonLoading: Boolean = false,

    val isUpsertButtonLoading: Boolean = false
) {
    val isContentLoading: Boolean get() =
        isHeroLoading &&
        isSkinLoading
}
