@file:OptIn(ExperimentalCoroutinesApi::class)

package com.a.injector.data.repository

import arrow.core.Either
import com.a.injector.R
import com.a.injector.data.remote.profile.ProfileDto
import com.a.injector.data.remote.auth.AuthApi
import com.a.injector.data.remote.profile.ProfileApi
import com.a.injector.domain.model.Text
import com.a.injector.data.util.catchAndDispatch
import com.a.injector.domain.model.state.AuthResult
import com.a.injector.domain.repository.AccountRepository
import io.github.jan.supabase.auth.user.UserInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import org.koin.core.annotation.Single
import kotlin.time.Duration.Companion.seconds
import kotlin.uuid.Uuid

@Single
class AccountRepositoryImpl(
    private val authApi: AuthApi,
    private val profileApi: ProfileApi
): AccountRepository {
    override val currentState: Flow<AuthResult> = authApi.getAuthState().map { userInfo ->
        when {
            userInfo == null -> AuthResult.Unauthenticated
            userInfo.isAnonymous == true -> AuthResult.Guest
            else -> AuthResult.Authenticated
        }
    }.flowOn(Dispatchers.IO)

    override fun getCurrent(): Flow<Either<Text, UserInfo>> {
        return flow<Either<Text, UserInfo>> {
            val result = authApi.getAuthState().map { userInfo ->
                if (userInfo != null) {
                    Either.Right(userInfo)
                } else {
                    Either.Left(Text.Resource(R.string.exception_no_data))
                }
            }
            emitAll(result)
        }.catchAndDispatch()
    }

    override fun signIn(email: String, password: String): Flow<Either<Text, Unit>> {
        return flow<Either<Text, Unit>> {
            authApi.signIn(
                email = email,
                password = password
            )
            emit(Either.Right(Unit))
        }.catchAndDispatch()
    }

    override fun signUp(email: String, password: String): Flow<Either<Text, Unit>> {
        return flow<Either<Text, Unit>> {
            authApi.signUp(
                email = email,
                password = password
            )
            delay(1.seconds)
            profileApi.upsert(
                profileDto = ProfileDto(
                    id = authApi.getAuthState().filterNotNull().first().id,
                    username = "user_${Uuid.random()}",
                )
            )
            emit(Either.Right(Unit))
        }.catchAndDispatch()
    }

    override fun signLink(email: String): Flow<Either<Text, Text>> {
        return flow<Either<Text, Text>> {
            authApi.signOtp(
                email = email
            )
            emit(Either.Right(Text.Resource(R.string.success_sign_link_email_sent)))
        }.catchAndDispatch()
    }

    override fun signGuest(): Flow<Either<Text, Unit>> {
        return flow<Either<Text, Unit>> {
            authApi.signGuest()
            emit(Either.Right(Unit))
        }.catchAndDispatch()
    }

    override fun signOut(): Flow<Either<Text, Unit>> {
        return flow<Either<Text, Unit>> {
            authApi.signOut()
            emit(Either.Right(Unit))
        }.catchAndDispatch()
    }

    override fun changePassword(password: String): Flow<Either<Text, Text>> {
        return flow<Either<Text, Text>> {
            authApi.changePassword(
                password = password
            )
            emit(Either.Right(Text.Resource(R.string.success_change_password)))
        }.catchAndDispatch()
    }
}