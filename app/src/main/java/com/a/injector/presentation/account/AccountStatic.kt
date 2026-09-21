package com.a.injector.presentation.account

import com.a.injector.R
import com.a.injector.domain.model.StaticModel

enum class ProfileAccountId {
    Email, Username, Contribution, Role
}

val profileAccountItems = listOf(
    StaticModel(
        id = ProfileAccountId.Email,
        contentTextResId = R.string.item_email
    ),
    StaticModel(
        id = ProfileAccountId.Username,
        contentTextResId = R.string.item_username
    ),
    StaticModel(
        id = ProfileAccountId.Contribution,
        contentTextResId = R.string.item_contribution
    ),
    StaticModel(
        id = ProfileAccountId.Role,
        contentTextResId = R.string.item_role
    )
)

enum class AdministratorMenuId {
    RoleManager, CleanStorage
}

val profileAdministratorMenuItems = listOf(
    StaticModel(
        id = AdministratorMenuId.RoleManager,
        contentTextResId = R.string.item_role_manager,
        supportingTextResId = R.string.item_role_manager_desc
    ),
    StaticModel(
        id = AdministratorMenuId.CleanStorage,
        contentTextResId = R.string.item_clean_storage,
        supportingTextResId = R.string.item_clean_storage_desc
    ),
)

enum class ManageAccountId {
    ChangePassword
}

val profileManageAccountItems = listOf(
    StaticModel(
        id = ManageAccountId.ChangePassword,
        contentTextResId = R.string.item_change_password,
        supportingTextResId = R.string.item_change_password_desc
    )
)