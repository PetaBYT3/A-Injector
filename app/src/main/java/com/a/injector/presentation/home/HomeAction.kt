package com.a.injector.presentation.home

import com.a.injector.domain.model.Text

sealed interface HomeAction {
    data object MaintenanceBottomSheet: HomeAction
    data object UpdateBottomSheet: HomeAction

    data class ShowSnackBar(val text: Text): HomeAction
}