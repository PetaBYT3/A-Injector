package com.a.injector.presentation.account

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AdminPanelSettings
import androidx.compose.material.icons.rounded.CleaningServices
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material.icons.rounded.Password
import androidx.compose.material.icons.rounded.PermIdentity
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Upload
import androidx.compose.material3.Icon
import com.a.injector.R
import com.a.injector.domain.model.StaticModel

enum class Profile {
    Email, Username, Contribution, Role
}

val profiles = listOf(
    StaticModel(
        id = Profile.Email,
        leadingContent = { Icon(Icons.Rounded.Email, null) },
        contentTextResId = R.string.item_email
    ),
    StaticModel(
        id = Profile.Username,
        leadingContent = { Icon(Icons.Rounded.Person, null) },
        contentTextResId = R.string.item_username
    ),
    StaticModel(
        id = Profile.Contribution,
        leadingContent = { Icon(Icons.Rounded.Upload, null) },
        contentTextResId = R.string.item_contribution
    ),
    StaticModel(
        id = Profile.Role,
        leadingContent = { Icon(Icons.Rounded.PermIdentity, null) },
        contentTextResId = R.string.item_role
    )
)

enum class AdministratorMenu {
    RoleManager, CleanStorage
}

val administratorMenus = listOf(
    StaticModel(
        id = AdministratorMenu.RoleManager,
        leadingContent = { Icon(Icons.Rounded.AdminPanelSettings, null) },
        contentTextResId = R.string.item_role_manager,
        supportingTextResId = R.string.item_role_manager_desc
    ),
    StaticModel(
        id = AdministratorMenu.CleanStorage,
        leadingContent = { Icon(Icons.Rounded.CleaningServices, null) },
        contentTextResId = R.string.item_clean_storage,
        supportingTextResId = R.string.item_clean_storage_desc
    ),
)

enum class ManageAccount {
    ChangePassword
}

val manageAccounts = listOf(
    StaticModel(
        id = ManageAccount.ChangePassword,
        leadingContent = { Icon(Icons.Rounded.Password, null) },
        contentTextResId = R.string.item_change_password,
        supportingTextResId = R.string.item_change_password_desc
    )
)