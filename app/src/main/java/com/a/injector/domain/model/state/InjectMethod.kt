package com.a.injector.domain.model.state

import androidx.annotation.StringRes
import com.a.injector.R

enum class InjectMethod(
    @StringRes val title: Int
) {
    StorageManager(R.string.storage_manager),
    Shizuku(R.string.shizuku),
    Superuser(R.string.superuser)
}