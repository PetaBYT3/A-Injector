package com.a.injector.presentation.managereplace

import com.a.injector.data.util.TextResource
import com.a.injector.domain.model.HeroModel
import com.a.injector.domain.model.ReplaceModel
import com.a.injector.domain.model.SkinModel
import io.github.vinceglb.filekit.PlatformFile

data class ManageReplaceState(
    val isOnEdit: Boolean = false,

    val isHeroLoading: Boolean = true,
    val isHeroError: TextResource? = null,
    val hero: HeroModel = HeroModel.EMPTY,

    val isSkinLoading: Boolean = true,
    val isSkinError: TextResource? = null,
    val skin: SkinModel = SkinModel.EMPTY,

    val isReplaceLoading: Boolean = true,
    val isReplaceError: TextResource? = null,
    val replace: ReplaceModel = ReplaceModel.EMPTY,
    val replaceFile: PlatformFile? = null,

    val isDeleteBottomSheetVisible: Boolean = false,
    val isDeleteButtonLoading: Boolean = false,

    val isUpsertButtonLoading: Boolean = false
) {
    val isContentLoading: Boolean get() =
        isHeroLoading &&
        isSkinLoading &&
        isReplaceLoading
}
