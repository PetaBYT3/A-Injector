package com.a.injector.domain.model

data class HeroDetailModel(
    val id: String,
    val name: String,
    val skins: List<SkinDetailModel> = emptyList()
) {
    companion object {
        val EMPTY = HeroDetailModel(
            id = "",
            name = "",
        )
    }
}
