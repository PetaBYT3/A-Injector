package com.a.injector.data.remote

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.SignOutScope
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.status.SessionStatus
import io.github.jan.supabase.auth.user.UserInfo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import org.koin.core.annotation.Single

@Single
class AuthApiImpl(
    private val supabaseClient: SupabaseClient
): AuthApi {
    override val currentAuth: Flow<UserInfo?> = supabaseClient.auth.sessionStatus.filter { sessionStatus ->
        sessionStatus !is SessionStatus.Initializing && sessionStatus !is SessionStatus.RefreshFailure
    }.map {
        supabaseClient.auth.currentUserOrNull()
    }

    override suspend fun signIn(email: String, password: String) {
        supabaseClient.auth.signInWith(
            provider = Email,
            config = {
                this.email = email
                this.password = password
            }
        )
    }

    override suspend fun signUp(email: String, password: String) {
        supabaseClient.auth.signUpWith(
            provider = Email,
            config = {
                this.email = email
                this.password = password
            }
        )
    }

    override suspend fun signGuest() {
        supabaseClient.auth.signInAnonymously()
    }

    override suspend fun signOut() {
        supabaseClient.auth.signOut(SignOutScope.GLOBAL)
    }
}