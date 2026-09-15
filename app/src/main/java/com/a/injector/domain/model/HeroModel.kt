package com.a.injector.domain.model

data class HeroModel(
    val id: String,
    val name: String
) {
    companion object {
        val EMPTY = HeroModel(
            id = "",
            name = ""
        )
    }
}
