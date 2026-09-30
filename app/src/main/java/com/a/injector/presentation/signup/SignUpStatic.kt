package com.a.injector.presentation.signup

import com.a.injector.R
import com.a.injector.domain.model.StaticModel

enum class PasswordRequirement {
    HasEightCharacter, ContainUppercase, ContainNumber
}

val passwordRequirements = listOf(
    StaticModel(
        id = PasswordRequirement.HasEightCharacter,
        content = R.string.eight_character
    ),
    StaticModel(
        id = PasswordRequirement.ContainUppercase,
        content = R.string.capital
    ),
    StaticModel(
        id = PasswordRequirement.ContainNumber,
        content = R.string.number
    )
)