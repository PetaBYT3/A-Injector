package com.a.injector.presentation.panelsupporting

import com.a.injector.domain.model.SupportModel

sealed interface PanelSupportingAction {
    data class ShowSupportingBottomSheet(val supportModel: SupportModel): PanelSupportingAction
    data object DismissSupportingBottomSheet: PanelSupportingAction
    data object DenySupportingButton: PanelSupportingAction
    data object ConfirmSupportingButton: PanelSupportingAction
}