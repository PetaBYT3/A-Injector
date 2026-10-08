package com.a.injector.domain.model

import com.a.injector.domain.model.state.InjectMethod

data class InjectModel(
    val method: InjectMethod,
    val isGranted: Boolean
) {
    companion object {
        val EMPTY = InjectModel(
            method = InjectMethod.StorageManager,
            isGranted = false
        )
    }
}
