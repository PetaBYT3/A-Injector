package com.a.injector.presentation.panelsupport

import com.a.injector.domain.model.SupportModel
import com.a.injector.domain.model.Text

data class PanelSupportState(
    val isSupportingListLoading: Boolean = true,
    val isSupportingListError: Text? = null,
    val supportingList: List<SupportModel> = emptyList(),

    val isSupportingBottomSheetVisible: Boolean = false,
    val supportingToAction: SupportModel = SupportModel.EMPTY,
    val proofUrlToAction: String? = null,
    val isActionSupportingButtonLoading: Boolean = false,
) {
    val isContentLoading: Boolean get() =
        isSupportingListLoading
}
