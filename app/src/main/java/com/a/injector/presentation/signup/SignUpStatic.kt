package com.a.injector.presentation.signup

import com.a.injector.R
import com.a.injector.domain.model.StaticModel

enum class PasswordRequirement {
    MoreThanEightCharacter, ContainUppercase, ContainNumber
}

val passwordRequirements = listOf(
    StaticModel(
        id = PasswordRequirement.MoreThanEightCharacter,
        contentTextResId = R.string.item_eight_character
    ),
    StaticModel(
        id = PasswordRequirement.ContainUppercase,
        contentTextResId = R.string.item_uppercase
    ),
    StaticModel(
        id = PasswordRequirement.ContainNumber,
        contentTextResId = R.string.item_number
    )
)