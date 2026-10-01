package com.a.injector.domain.repository

import arrow.core.Either
import com.a.injector.domain.model.Text
import com.a.injector.domain.model.state.AuthResult
import io.github.jan.supabase.auth.user.UserInfo
import kotlinx.coroutines.flow.Flow

interface AccountRepository {
    val currentState: Flow<AuthResult>

    fun getCurrent(): Flow<Either<Text, UserInfo>>
    fun signIn(email: String, password: String): Flow<Either<Text, Unit>>
    fun signUp(email: String, password: String): Flow<Either<Text, Unit>>
    fun signLink(email: String): Flow<Either<Text, Text>>
    fun signGuest(): Flow<Either<Text, Unit>>
    fun signOut(): Flow<Either<Text, Unit>>

    fun changePassword(password: String): Flow<Either<Text, Text>>
}