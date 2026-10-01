package com.a.injector.presentation.managereplace

import com.a.injector.domain.model.HeroModel
import com.a.injector.domain.model.ReplaceModel
import com.a.injector.domain.model.SkinModel
import com.a.injector.domain.model.Text
import io.github.vinceglb.filekit.PlatformFile

data class ManageReplaceState(
    val isOnEdit: Boolean = false,

    val isProfileLoading: Boolean = true,
    val isDeleteEnabled: Boolean = false,

    val isHeroLoading: Boolean = true,
    val isHeroError: Text? = null,
    val hero: HeroModel = HeroModel.EMPTY,

    val isSkinLoading: Boolean = true,
    val isSkinError: Text? = null,
    val skin: SkinModel = SkinModel.EMPTY,

    val isReplaceLoading: Boolean = true,
    val isReplaceError: Text? = null,
    val replace: ReplaceModel = ReplaceModel.EMPTY,
    val replaceFile: PlatformFile? = null,

    val isDeleteBottomSheetVisible: Boolean = false,
    val isDeleteButtonLoading: Boolean = false,

    val isUpsertButtonLoading: Boolean = false
) {
    val isContentLoading: Boolean get() =
        isProfileLoading ||
        isHeroLoading ||
        isSkinLoading ||
        isReplaceLoading
}
