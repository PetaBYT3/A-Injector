package com.a.injector.presentation.landing

sealed interface LandingAction {
    data object ButtonSignGuest: LandingAction
}