package com.a.injector.domain.model

import com.a.injector.domain.model.state.InjectMethod

data class InjectModel(
    val injectMethod: InjectMethod,
    val isGranted: Boolean
) {
    companion object {
        val EMPTY = InjectModel(
            injectMethod = InjectMethod.StorageManager,
            isGranted = false
        )
    }
}
