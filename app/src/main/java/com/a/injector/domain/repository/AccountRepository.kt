package com.a.injector.domain.repository

import arrow.core.Either
import com.a.injector.data.util.TextResource
import com.a.injector.domain.model.state.AuthResult
import io.github.jan.supabase.auth.user.UserInfo
import kotlinx.coroutines.flow.Flow

interface AccountRepository {
    val currentAuthState: Flow<AuthResult>

    fun getCurrent(): Flow<Either<TextResource, UserInfo>>
    fun signIn(email: String, password: String): Flow<Either<TextResource, Unit>>
    fun signUp(email: String, password: String): Flow<Either<TextResource, Unit>>
    fun signLink(email: String): Flow<Either<TextResource, TextResource>>
    fun signGuest(): Flow<Either<TextResource, Unit>>
    fun signOut(): Flow<Either<TextResource, Unit>>

    fun changePassword(password: String): Flow<Either<TextResource, TextResource>>
}