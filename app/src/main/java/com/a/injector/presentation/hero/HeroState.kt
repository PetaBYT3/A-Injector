package com.a.injector.presentation.hero

import com.a.injector.data.util.TextResource
import com.a.injector.domain.model.HeroDetailModel
import com.a.injector.domain.model.SkinDetailModel

data class HeroState(
    val isProfileLoading: Boolean = true,
    val isModifyEnabled: Boolean = false,

    val isHeroDetailLoading: Boolean = true,
    val isHeroDetailError: TextResource? = null,
    val heroDetail: HeroDetailModel = HeroDetailModel.EMPTY,

    val isActionSkinBottomSheetVisible: Boolean = false,
    val skinToAction: SkinDetailModel = SkinDetailModel.EMPTY,

    val isInjectLoading: Map<String, Unit> = emptyMap()
) {
    val isContentLoading: Boolean get() =
        isProfileLoading &&
        isHeroDetailLoading
}
