package com.a.injector.presentation.managehero

sealed interface ManageHeroAction {
    data class HeroNameTextField(val name: String): ManageHeroAction

    data object DeleteBottomSheet: ManageHeroAction
    data object DeleteButton: ManageHeroAction

    data object UpsertButton: ManageHeroAction
}