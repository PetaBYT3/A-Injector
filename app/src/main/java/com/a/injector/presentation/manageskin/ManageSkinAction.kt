package com.a.injector.presentation.manageskin

sealed interface ManageSkinAction {
    data class SkinLabelTextField(val label: String): ManageSkinAction
    data class SkinNameTextField(val name: String): ManageSkinAction

    data object DeleteBottomSheet: ManageSkinAction
    data object DeleteButton: ManageSkinAction

    data object UpsertButton: ManageSkinAction
}