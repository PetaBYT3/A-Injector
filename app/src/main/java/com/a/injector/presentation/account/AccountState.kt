package com.a.injector.presentation.account

import com.a.injector.domain.model.ProfileModel
import com.a.injector.domain.model.RoleModel
import io.github.jan.supabase.auth.user.UserInfo

data class AccountState(
    val isUserInfoLoading: Boolean = true,
    val userInfo: UserInfo? = null,

    val isProfileLoading: Boolean = true,
    val profile: ProfileModel = ProfileModel.EMPTY,

    val isRequestLoading: Boolean = true,
    val request: RoleModel = RoleModel.EMPTY,

    val isUpsertProfileBottomSheetVisible: Boolean = false,
    val profileToUpsert: ProfileModel = ProfileModel.EMPTY,
    val isUpsertProfileButtonLoading: Boolean = false,

    val isRequestRoleBottomSheetVisible: Boolean = false,

    val isCleanStorageBottomSheetVisible: Boolean = false,
    val isCleanStorageButtonLoading: Boolean = false,

    val isChangePasswordBottomSheetVisible: Boolean = false,
    val newPasswordTextField: String = "",
    val isChangePasswordButtonLoading: Boolean = false,

    val isSignOutBottomSheetVisible: Boolean = false,
    val isSingOutButtonLoading: Boolean = false
) {
    val isContentLoading: Boolean get() =
        isUserInfoLoading &&
        isRequestLoading &&
        isProfileLoading

    val isPasswordMoreThan8Character: Boolean get() = newPasswordTextField.length >= 8
    val isPasswordContainUppercase: Boolean get() = newPasswordTextField.any { it.isUpperCase() }
    val isPasswordContainNumber: Boolean get() = newPasswordTextField.any { it.isDigit() }

    val isPasswordValid: Boolean get() =
        newPasswordTextField.isNotEmpty() &&
        isPasswordMoreThan8Character &&
        isPasswordContainUppercase &&
        isPasswordContainNumber
}
