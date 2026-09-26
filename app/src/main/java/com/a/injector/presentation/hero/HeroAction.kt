package com.a.injector.presentation.hero

import com.a.injector.domain.model.ReplaceModel
import com.a.injector.domain.model.SkinModel

sealed interface HeroAction {
    data class ShowActionSkinBottomSheet(val skin: SkinModel): HeroAction
    data object DismissSkinActionBottomSheet: HeroAction

    data class StartInject(
        val skin: SkinModel,
        val replace: ReplaceModel
    ): HeroAction
}