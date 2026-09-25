package com.a.injector.presentation.landing

import com.a.injector.R
import com.a.injector.domain.model.StaticModel

enum class SignOption {
    SignIn, SignUp, Guest
}

val signOptions = listOf(
    StaticModel(
        id = SignOption.SignIn,
        contentTextResId = R.string.action_sign_in
    ),
    StaticModel(
        id = SignOption.SignUp,
        contentTextResId = R.string.action_sign_up
    ),
    StaticModel(
        id = SignOption.Guest,
        contentTextResId = R.string.action_sign_guest
    )
)