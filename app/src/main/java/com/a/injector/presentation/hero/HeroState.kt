package com.a.injector.presentation.hero

import com.a.injector.data.util.TextResource
import com.a.injector.domain.model.HeroModel
import com.a.injector.domain.model.ReplaceModel
import com.a.injector.domain.model.SkinModel

data class HeroState(
    val isProfileLoading: Boolean = true,
    val isModifyEnabled: Boolean = false,

    val isHeroDetailLoading: Boolean = true,
    val isHeroDetailError: TextResource? = null,
    val heroDetail: HeroModel = HeroModel.EMPTY,

    val isActionSkinBottomSheetVisible: Boolean = false,
    val skinToAction: SkinModel = SkinModel.EMPTY,

    val isInjectBottomSheetVisible: Boolean = false,
    val targetSkin: SkinModel = SkinModel.EMPTY,
    val targetReplace: ReplaceModel = ReplaceModel.EMPTY,
    val injectStatus: TextResource = TextResource.DynamicString(""),
) {
    val isContentLoading: Boolean get() =
        isProfileLoading &&
        isHeroDetailLoading
}
