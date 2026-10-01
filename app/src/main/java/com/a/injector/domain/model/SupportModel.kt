package com.a.injector.domain.model

data class SupportModel(
    val id: String,
    val support: Long,
    val imageUrl: String,
    val profile: ProfileModel
) {
    companion object {
        val EMPTY = SupportModel(
            id = "",
            support = 0,
            imageUrl = "",
            profile = ProfileModel.EMPTY
        )
    }
}
