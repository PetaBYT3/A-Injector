package com.a.injector.domain.model

data class HeroModel(
    val id: String,
    val name: String,
    val skins: List<SkinModel> = emptyList()
) {
    companion object {
        val EMPTY = HeroModel(
            id = "",
            name = "",
        )
    }
}
