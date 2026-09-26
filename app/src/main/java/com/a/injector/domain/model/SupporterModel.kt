package com.a.injector.domain.model

data class SupporterModel(
    val id: String,
    val nominal: Long,
    val profile: ProfileModel
) {
    companion object {
        val EMPTY = SupporterModel(
            id = "",
            nominal = 0,
            profile = ProfileModel.EMPTY
        )
    }
}
