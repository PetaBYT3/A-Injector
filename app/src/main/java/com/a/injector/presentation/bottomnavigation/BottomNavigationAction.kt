package com.a.injector.presentation.bottomnavigation

sealed interface BottomNavigationAction {
    data object MaintenanceBottomSheet: BottomNavigationAction
    data object UpdateBottomSheet: BottomNavigationAction
}