package com.a.injector.presentation.panelsupporting

import com.a.injector.domain.model.SupportingModel

sealed interface PanelSupportingAction {
    data class ShowSupportingBottomSheet(val supportingModel: SupportingModel): PanelSupportingAction
    data object DismissSupportingBottomSheet: PanelSupportingAction
    data object DenySupportingButton: PanelSupportingAction
    data object ConfirmSupportingButton: PanelSupportingAction
}