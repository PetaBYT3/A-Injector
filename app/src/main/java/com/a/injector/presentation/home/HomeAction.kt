package com.a.injector.presentation.home

import com.a.injector.data.local.CommandService

sealed interface HomeAction {
    data class SetCommandServiceButton(val commandService: CommandService): HomeAction
}