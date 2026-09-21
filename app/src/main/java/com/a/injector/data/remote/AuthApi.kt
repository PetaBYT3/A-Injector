package com.a.injector.data.remote

import io.github.jan.supabase.auth.user.UserInfo
import kotlinx.coroutines.flow.Flow

interface AuthApi {
    val currentAuth: Flow<UserInfo?>
    suspend fun signIn(email: String, password: String)
    suspend fun signUp(email: String, password: String)
    suspend fun signGuest()
    suspend fun signOut()

    suspend fun sendResetPassword(email: String)
}