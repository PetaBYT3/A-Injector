package com.a.injector.domain.model.state

import com.a.injector.R
import com.a.injector.domain.model.Text
import kotlinx.serialization.Serializable

@Serializable
enum class Role(
    val title: Text,
    val desc: Text
) {
    Administrator(
        Text.Resource(R.string.administrator),
        Text.Resource(R.string.administrator_desc)
    ),
    Manager(
        Text.Resource(R.string.manager),
        Text.Resource(R.string.manager_desc)
    ),
    Contributor(
        Text.Resource(R.string.contributor),
        Text.Resource(R.string.contributor_desc)
    ),
    User(
        Text.Resource(R.string.user),
        Text.Resource(R.string.user_desc)
    );

    val modifyEnabled: Boolean get() = this != User
    val deleteEnabled: Boolean get() = this in setOf(Administrator, Manager)
}