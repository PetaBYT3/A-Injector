package com.a.injector.presentation.landing

import com.a.injector.R
import com.a.injector.domain.model.StaticModel

enum class LandingOptionId {
    SignIn, SignUp, Guest
}

val landingOptionItems = listOf(
    StaticModel(
        id = LandingOptionId.SignIn,
        contentTextResId = R.string.action_sign_in
    ),
    StaticModel(
        id = LandingOptionId.SignUp,
        contentTextResId = R.string.action_sign_up
    ),
    StaticModel(
        id = LandingOptionId.Guest,
        contentTextResId = R.string.action_sign_guest
    )
)