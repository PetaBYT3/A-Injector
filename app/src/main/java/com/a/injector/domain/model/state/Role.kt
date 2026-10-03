package com.a.injector.domain.model.state

import com.a.injector.R
import kotlinx.serialization.Serializable

@Serializable
enum class Role(
    val title: Int,
    val desc: Int
) {
    Administrator(R.string.administrator, R.string.administrator_desc),
    Manager(R.string.manager, R.string.manager_desc),
    Contributor(R.string.contributor, R.string.contributor_desc),
    User(R.string.user, R.string.user_desc),
    Unknown(R.string.unknown, R.string.unknown);

    val modifyEnabled: Boolean get() = this != User
    val deleteEnabled: Boolean get() = this in setOf(Administrator, Manager)

    companion object {
        val allowedRoleToRequest = Role.entries.filterNot { role ->
            role == Administrator || role == Unknown
        }

        val allRole = Role.entries.filterNot { role ->
            role == Unknown
        }
    }
}