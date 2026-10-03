package com.a.injector.presentation.account

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material.icons.rounded.Password
import androidx.compose.material.icons.rounded.PermIdentity
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Upload
import androidx.compose.material3.Icon
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import com.a.injector.R
import com.a.injector.domain.model.StaticModel

enum class Profile {
    Email, Username, Contribution, Supporting, ProfileRole
}

val profiles = listOf(
    StaticModel(
        id = Profile.Email,
        leadingContent = { Icon(Icons.Rounded.Email, null) },
        content = R.string.email,
    ),
    StaticModel(
        id = Profile.Username,
        leadingContent = { Icon(Icons.Rounded.Person, null) },
        content = R.string.username,
    ),
    StaticModel(
        id = Profile.Contribution,
        leadingContent = { Icon(Icons.Rounded.Upload, null) },
        content = R.string.contribution
    ),
    StaticModel(
        id = Profile.Supporting,
        leadingContent = { Icon(ImageVector.vectorResource(R.drawable.support), null) },
        content = R.string.support
    ),
    StaticModel(
        id = Profile.ProfileRole,
        leadingContent = { Icon(Icons.Rounded.PermIdentity, null) },
        content = R.string.role
    )
)

enum class ManageAccount {
    TerminateSession, ChangePassword
}

val manageAccounts = listOf(
    StaticModel(
        id = ManageAccount.TerminateSession,
        leadingContent = { Icon(Icons.Rounded.Close, null) },
        content = R.string.terminate_session,
        supportingContent = R.string.terminate_session_desc
    ),
    StaticModel(
        id = ManageAccount.ChangePassword,
        leadingContent = { Icon(Icons.Rounded.Password, null) },
        content = R.string.change_password,
        supportingContent = R.string.change_password_desc
    )
)