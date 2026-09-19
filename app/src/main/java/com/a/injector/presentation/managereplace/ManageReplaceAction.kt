package com.a.injector.presentation.managereplace

import io.github.vinceglb.filekit.PlatformFile

sealed interface ManageReplaceAction {
    data class LabelTextField(val label: String): ManageReplaceAction
    data class NameTextField(val name: String): ManageReplaceAction
    data class ReplaceFilePicker(val platformFile: PlatformFile?): ManageReplaceAction

    data object DeleteBottomSheet: ManageReplaceAction
    data object DeleteButton: ManageReplaceAction

    data object UpsertButton: ManageReplaceAction
}