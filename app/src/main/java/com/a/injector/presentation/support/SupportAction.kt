package com.a.injector.presentation.support

import io.github.vinceglb.filekit.PlatformFile

sealed interface SupportAction {
    data class NominalTextField(val nominal: String): SupportAction
    data class ImagePicker(val image: PlatformFile): SupportAction

    data object UpsertSupportingButton: SupportAction
}