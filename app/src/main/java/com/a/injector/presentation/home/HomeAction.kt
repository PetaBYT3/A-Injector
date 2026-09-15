package com.a.injector.presentation.home

import com.a.injector.data.dto.Executor

sealed interface HomeAction {
    data class SetExecutor(val executor: Executor): HomeAction
}