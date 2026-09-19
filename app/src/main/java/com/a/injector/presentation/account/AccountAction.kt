package com.a.injector.presentation.account

import com.a.injector.domain.model.ProfileModel

sealed interface AccountAction {
    data class ShowUpsertProfileBottomSheet(val profileModel: ProfileModel): AccountAction
    data object DismissUpsertProfileBottomSheet: AccountAction
    data class UsernameTextField(val username: String): AccountAction
    data object UpsertProfileButton: AccountAction

    data object RequestContributorButton: AccountAction

    data object SignOutBottomSheet: AccountAction
    data object SignOutButton: AccountAction
}