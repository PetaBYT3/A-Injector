package com.a.injector.data.remote

import kotlin.time.Duration.Companion.milliseconds

object SupabaseConst {
    const val SCHEMA = "public"

    const val VERSION_TABLE = "version"
    const val PROFILE_TABLE = "profile"
    const val ROLE_TABLE = "role"
    const val SUPPORT_TABLE = "support"
    const val HERO_TABLE = "hero"
    const val SKIN_TABLE = "skin"
    const val REPLACE_TABLE = "replace"

    const val IS_DEBUG_ENABLED = false

    val DEBOUNCE = 150.milliseconds
}