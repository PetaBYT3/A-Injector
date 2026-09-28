package com.a.injector.presentation.supporting

import io.github.vinceglb.filekit.PlatformFile

sealed interface SupportingAction {
    data class NominalTextField(val nominal: String): SupportingAction
    data class ImagePicker(val image: PlatformFile): SupportingAction

    data object UpsertSupportingButton: SupportingAction
}