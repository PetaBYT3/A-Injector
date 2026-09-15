package com.a.injector.domain.model

data class SkinModel(
    val id: String,
    val heroId: String,
    val label: String,
    val name: String
) {
    companion object {
        val EMPTY = SkinModel(
            id = "",
            heroId = "",
            label = "",
            name = ""
        )
    }
}
