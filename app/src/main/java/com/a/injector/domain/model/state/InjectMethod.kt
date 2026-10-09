package com.a.injector.domain.model.state

import androidx.annotation.StringRes
import com.a.injector.R

enum class InjectMethod(
    @StringRes val title: Int
) {
    Shizuku(R.string.shizuku),
    Superuser(R.string.superuser);

    companion object {
        fun fromString(name: String?): InjectMethod {
            if (name == null) {
                return Shizuku
            }
            return entries.firstOrNull { injectMethod ->
                injectMethod.name.equals(name, ignoreCase = true)
            } ?: Shizuku
        }
    }
}