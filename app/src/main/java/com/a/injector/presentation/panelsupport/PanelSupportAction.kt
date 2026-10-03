package com.a.injector.presentation.panelsupport

import com.a.injector.domain.model.SupportModel

sealed interface PanelSupportAction {
    data class ShowSupportBottomSheet(val supportModel: SupportModel): PanelSupportAction
    data object DismissSupportBottomSheet: PanelSupportAction
    data object DenySupportButton: PanelSupportAction
    data object ConfirmSupportButton: PanelSupportAction
}