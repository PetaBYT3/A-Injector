package com.a.injector.presentation.script

import com.a.injector.domain.model.HeroModel
import com.a.injector.domain.model.Text

data class ScriptState(
    val isProfileLoading: Boolean = true,
    val isModifyEnabled: Boolean = false,

    val isHeroesLoading: Boolean = true,
    val isHeroesError: Text? = null,
    val heroes: List<HeroModel> = emptyList(),
    val filteredHeroes: List<HeroModel> = emptyList(),

    val searchTextField: String = "",
) {
    val isContentLoading: Boolean get() =
        isProfileLoading ||
        isHeroesLoading
}
