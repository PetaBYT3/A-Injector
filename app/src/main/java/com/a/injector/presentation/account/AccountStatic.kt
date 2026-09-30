package com.a.injector.presentation.account

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AdminPanelSettings
import androidx.compose.material.icons.rounded.CleaningServices
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material.icons.rounded.Password
import androidx.compose.material.icons.rounded.PermIdentity
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.PersonAdd
import androidx.compose.material.icons.rounded.Upload
import androidx.compose.material3.Icon
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import com.a.injector.R
import com.a.injector.data.util.TextResource
import com.a.injector.domain.model.StaticModel

enum class Profile {
    Email, Username, Contribution, Supporting, ProfileRole
}

val profiles = listOf(
    StaticModel(
        id = Profile.Email,
        leadingContent = { Icon(Icons.Rounded.Email, null) },
        content = TextResource.StringResource(R.string.email),
    ),
    StaticModel(
        id = Profile.Username,
        leadingContent = { Icon(Icons.Rounded.Person, null) },
        content = TextResource.StringResource(R.string.username),
    ),
    StaticModel(
        id = Profile.Contribution,
        leadingContent = { Icon(Icons.Rounded.Upload, null) },
        content = TextResource.StringResource(R.string.contribution)
    ),
    StaticModel(
        id = Profile.Supporting,
        leadingContent = { Icon(ImageVector.vectorResource(R.drawable.support), null) },
        content = TextResource.StringResource(R.string.support),
    ),
    StaticModel(
        id = Profile.ProfileRole,
        leadingContent = { Icon(Icons.Rounded.PermIdentity, null) },
        content = TextResource.StringResource(R.string.role),
    )
)

enum class AdministratorMenu {
    PanelSupporting, RoleManager, UserPanel, CleanStorage
}

val administratorMenus = listOf(
    StaticModel(
        id = AdministratorMenu.PanelSupporting,
        leadingContent = { Icon(ImageVector.vectorResource(R.drawable.support), null) },
        content = TextResource.StringResource(R.string.support_panel),
        supportingContent = TextResource.StringResource(R.string.support_panel_desc)
    ),
    StaticModel(
        id = AdministratorMenu.RoleManager,
        leadingContent = { Icon(Icons.Rounded.AdminPanelSettings, null) },
        content = TextResource.StringResource(R.string.role_panel),
        supportingContent = TextResource.StringResource(R.string.role_panel_desc)
    ),
    StaticModel(
        id = AdministratorMenu.UserPanel,
        leadingContent = { Icon(Icons.Rounded.PersonAdd, null) },
        content = TextResource.StringResource(R.string.user_panel),
        supportingContent = TextResource.StringResource(R.string.user_panel_desc)
    ),
    StaticModel(
        id = AdministratorMenu.CleanStorage,
        leadingContent = { Icon(Icons.Rounded.CleaningServices, null) },
        content = TextResource.StringResource(R.string.clean_cloud_storage_desc),
        supportingContent = TextResource.StringResource(R.string.clean_cloud_storage_desc)
    ),
)

enum class ManageAccount {
    ChangePassword
}

val manageAccounts = listOf(
    StaticModel(
        id = ManageAccount.ChangePassword,
        leadingContent = { Icon(Icons.Rounded.Password, null) },
        content = TextResource.StringResource(R.string.change_password),
        supportingContent = TextResource.StringResource(R.string.change_password_desc)
    )
)