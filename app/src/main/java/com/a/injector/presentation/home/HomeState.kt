package com.a.injector.presentation.home

import com.a.injector.data.dto.Executor

data class HomeState(
    val executor: Executor = Executor.Shizuku,

    val isAuthorized: Boolean = false,
    val isGranted: Boolean = false,
)
