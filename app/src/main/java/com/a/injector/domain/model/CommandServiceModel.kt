package com.a.injector.domain.model

import com.a.injector.domain.model.state.CommandService

data class CommandServiceModel(
    val name: CommandService,
    val isRunning: Boolean
) {
    companion object {
        val DEFAULT = CommandServiceModel(
            name = CommandService.Shizuku,
            isRunning = false
        )
    }
}
