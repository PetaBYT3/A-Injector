package com.a.injector.data.dto

import com.a.injector.R
import com.a.injector.data.util.TextResource
import kotlinx.serialization.Serializable

@Serializable
enum class Role(
    val title: TextResource,
    val desc: TextResource
) {
    Administrator(
        TextResource.StringResource(R.string.administrator),
        TextResource.StringResource(R.string.administrator_desc)
    ),
    Manager(
        TextResource.StringResource(R.string.manager),
        TextResource.StringResource(R.string.manager_desc)
    ),
    Contributor(
        TextResource.StringResource(R.string.contributor),
        TextResource.StringResource(R.string.contributor_desc)
    ),
    User(
        TextResource.StringResource(R.string.user),
        TextResource.StringResource(R.string.user_desc)
    );

    val modifyEnabled: Boolean get() = this != User
    val deleteEnabled: Boolean get() = this in setOf(Administrator, Manager)
}