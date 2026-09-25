package com.a.injector.data.remote

import io.github.jan.supabase.auth.user.UserInfo
import kotlinx.coroutines.flow.Flow

interface AuthApi {
    val currentAuth: Flow<UserInfo?>
    suspend fun signIn(email: String, password: String)
    suspend fun signOtp(email: String)
    suspend fun verifyOtp(email: String, otp: String)
    suspend fun signUp(email: String, password: String)
    suspend fun signGuest()
    suspend fun signOut()
    suspend fun changePassword(password: String)

    suspend fun sendResetPassword(email: String)
}