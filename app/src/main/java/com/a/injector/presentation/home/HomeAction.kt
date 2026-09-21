package com.a.injector.presentation.home

import com.a.injector.domain.model.state.CommandService

sealed interface HomeAction {
    data class SetCommandServiceButton(val commandService: CommandService): HomeAction
}