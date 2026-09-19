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

enum class ManageAccountId {
    ChangePassword, DeleteAccount
}

val manageAccountItems = listOf(
    StaticModel(
        id = ManageAccountId.ChangePassword,
        contentTextResId = R.string.item_change_password,
        supportingTextResId = R.string.item_change_password_desc
    ),
    StaticModel(
        id = ManageAccountId.DeleteAccount,
        contentTextResId = R.string.item_delete_account,
        supportingTextResId = R.string.item_delete_account_desc
    )
)