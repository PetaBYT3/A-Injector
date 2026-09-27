package com.a.injector.data.util

import kotlin.time.Duration.Companion.milliseconds

object SupabaseElement {
    const val SCHEMA = "public"

    const val VERSION_TABLE = "version"
    const val PROFILE_TABLE = "profile"
    const val ROLE_TABLE = "role"
    const val SUPPORTING_TABLE = "supporting"
    const val HERO_TABLE = "hero"
    const val SKIN_TABLE = "skin"
    const val REPLACE_TABLE = "replace"

    val DEBOUNCE = 150.milliseconds
}