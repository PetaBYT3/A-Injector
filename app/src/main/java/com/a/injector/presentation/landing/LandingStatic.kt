package com.a.injector.presentation.landing

import com.a.injector.R
import com.a.injector.domain.model.StaticModel

enum class SignOption {
    SignIn, SignUp, Guest
}

val signOptions = listOf(
    StaticModel(
        id = SignOption.SignIn,
        content = R.string.sign_in
    ),
    StaticModel(
        id = SignOption.SignUp,
        content = R.string.sign_up
    ),
    StaticModel(
        id = SignOption.Guest,
        content = R.string.sign_guest
    )
)