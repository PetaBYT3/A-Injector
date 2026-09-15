package com.a.injector.presentation.hero

import com.a.injector.domain.model.HeroDetailModel
import com.a.injector.domain.model.SkinDetailModel

data class HeroState(
    val isHeroDetailLoading: Boolean = true,
    val isHeroDetailError: String? = null,
    val heroDetail: HeroDetailModel = HeroDetailModel.EMPTY,

    val isActionSkinBottomSheetVisible: Boolean = false,
    val skinToAction: SkinDetailModel = SkinDetailModel.EMPTY,

    val isInjectLoading: Map<String, Unit> = emptyMap()
) {
    val isContentLoading: Boolean get() =
        isHeroDetailLoading
}
