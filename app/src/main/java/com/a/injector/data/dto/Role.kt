package com.a.injector.data.dto

import kotlinx.serialization.Serializable

@Serializable
enum class Role {
    Administrator,
    Manager,
    Contributor,
    User
}