package com.a.injector.domain.model

data class SkinDetailModel(
    val id: String,
    val heroId: String,
    val label: String,
    val name: String,
    val replaces: List<ReplaceModel> = emptyList()
) {
    companion object {
        val EMPTY = SkinDetailModel(
            id = "",
            heroId = "",
            label = "",
            name = ""
        )
    }
}
