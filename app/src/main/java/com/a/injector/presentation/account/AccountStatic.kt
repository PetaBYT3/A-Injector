package com.a.injector.presentation.account

import com.a.injector.R
import com.a.injector.domain.model.StaticModel

enum class ProfileAccountId {
    Username, Contribution, Role
}

val profileAccountItems = listOf(
    StaticModel(
        id = ProfileAccountId.Username,
        contentTextResId = R.string.account_username
    ),
    StaticModel(
        id = ProfileAccountId.Contribution,
        contentTextResId = R.string.account_contribution
    ),
    StaticModel(
        id = ProfileAccountId.Role,
        contentTextResId = R.string.account_role
    )
)

enum class ManageAccountId {
    ChangePassword, DeleteAccount
}

val manageAccountItems = listOf(
    StaticModel(
        id = ManageAccountId.ChangePassword,
        contentTextResId = R.string.account_change_password,
        supportingTextResId = R.string.account_change_password_desc
    ),
    StaticModel(
        id = ManageAccountId.DeleteAccount,
        contentTextResId = R.string.account_delete_account,
        supportingTextResId = R.string.account_delete_account_desc
    )
)