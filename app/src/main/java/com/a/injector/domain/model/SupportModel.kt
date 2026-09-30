package com.a.injector.domain.model

data class SupportModel(
    val id: String,
    val nominal: Long,
    val imageUrl: String,
    val profile: ProfileModel
) {
    companion object {
        val EMPTY = SupportModel(
            id = "",
            nominal = 0,
            imageUrl = "",
            profile = ProfileModel.EMPTY
        )
    }
}
