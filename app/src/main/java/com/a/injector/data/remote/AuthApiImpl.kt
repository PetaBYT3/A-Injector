package com.a.injector.data.remote

import com.a.injector.domain.model.state.AuthState
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.SignOutScope
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.user.UserInfo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.koin.core.annotation.Single

@Single
class AuthApiImpl(
    private val supabaseClient: SupabaseClient
): AuthApi {
    private val _currentAuth = supabaseClient.auth.sessionStatus.map {
        supabaseClient.auth.currentUserOrNull()
    }
    override val currentAuth: Flow<UserInfo?> = _currentAuth

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