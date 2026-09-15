package com.a.injector.presentation.hero

import com.a.injector.domain.model.ReplaceModel
import com.a.injector.domain.model.SkinDetailModel

sealed interface HeroAction {
    data class ShowActionSkinBottomSheet(val skin: SkinDetailModel): HeroAction
    data object DismissSkinActionBottomSheet: HeroAction

    data class StartInject(val replace: ReplaceModel): HeroAction
}