package com.a.injector.presentation.panelsupporting

import com.a.injector.data.util.TextResource
import com.a.injector.domain.model.SupportModel

data class PanelSupportingState(
    val isSupportingListLoading: Boolean = true,
    val isSupportingListError: TextResource? = null,
    val supportingList: List<SupportModel> = emptyList(),

    val isSupportingBottomSheetVisible: Boolean = false,
    val supportingToAction: SupportModel = SupportModel.EMPTY,
    val proofUrlToAction: String? = null,
    val isActionSupportingButtonLoading: Boolean = false,
) {
    val isContentLoading: Boolean get() =
        isSupportingListLoading
}
