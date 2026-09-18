package com.a.injector.presentation.signup

import com.a.injector.R
import com.a.injector.domain.model.StaticModel

enum class SignUpPasswordRequirementId {
    MoreThanEightCharacter, ContainUppercase, ContainNumber
}

val signUpPasswordRequirementItem = listOf(
    StaticModel(
        id = SignUpPasswordRequirementId.MoreThanEightCharacter,
        contentTextResId = R.string.sign_up_8_character
    ),
    StaticModel(
        id = SignUpPasswordRequirementId.ContainUppercase,
        contentTextResId = R.string.sign_up_uppercase
    ),
    StaticModel(
        id = SignUpPasswordRequirementId.ContainNumber,
        contentTextResId = R.string.sign_up_number
    )
)