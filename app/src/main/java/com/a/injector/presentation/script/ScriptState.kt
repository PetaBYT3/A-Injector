package com.a.injector.presentation.script

import com.a.injector.domain.model.HeroModel

data class ScriptState(
    val isHeroesLoading: Boolean = true,
    val isHeroesError: String? = null,
    val heroes: List<HeroModel> = emptyList(),
    val filteredHeroes: List<HeroModel> = emptyList(),

    val searchTextField: String = "",
) {
    val isContentLoading: Boolean get() =
        isHeroesLoading
}
