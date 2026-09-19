package com.a.injector.data.util

import kotlin.time.Duration.Companion.milliseconds

object SupabaseConstanta {
    const val SCHEMA = "public"

    const val VERSION_TABLE = "version"
    const val PROFILE_TABLE = "profile"
    const val REQUEST_TABLE = "request"
    const val HERO_TABLE = "hero"
    const val SKIN_TABLE = "skin"
    const val REPLACE_TABLE = "replace"

    val DEBOUNCE = 150.milliseconds
}