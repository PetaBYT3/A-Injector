package com.a.injector.domain.model

data class SupportingModel(
    val id: String,
    val nominal: Long,
    val imageUrl: String,
    val profile: ProfileModel
) {
    companion object {
        val EMPTY = SupportingModel(
            id = "",
            nominal = 0,
            imageUrl = "",
            profile = ProfileModel.EMPTY
        )
    }
}
