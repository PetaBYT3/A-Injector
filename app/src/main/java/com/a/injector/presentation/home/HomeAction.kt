package com.a.injector.presentation.home

import com.a.injector.data.util.TextResource
import com.a.injector.domain.model.state.CommandService

sealed interface HomeAction {
    data object MaintenanceBottomSheet: HomeAction
    data object UpdateBottomSheet: HomeAction

    data class SetCommandServiceButton(val commandService: CommandService): HomeAction
    data object SupportBottomSheet: HomeAction

    data class ShowSnackBar(val textResource: TextResource): HomeAction
}