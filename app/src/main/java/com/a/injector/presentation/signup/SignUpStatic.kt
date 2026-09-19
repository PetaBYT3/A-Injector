package com.a.injector.presentation.signup

import com.a.injector.R
import com.a.injector.domain.model.StaticModel

enum class SignUpPasswordRequirementId {
    MoreThanEightCharacter, ContainUppercase, ContainNumber
}

val signUpPasswordRequirementItem = listOf(
    StaticModel(
        id = SignUpPasswordRequirementId.MoreThanEightCharacter,
        contentTextResId = R.string.item_eight_character
    ),
    StaticModel(
        id = SignUpPasswordRequirementId.ContainUppercase,
        contentTextResId = R.string.item_uppercase
    ),
    StaticModel(
        id = SignUpPasswordRequirementId.ContainNumber,
        contentTextResId = R.string.item_number
    )
)