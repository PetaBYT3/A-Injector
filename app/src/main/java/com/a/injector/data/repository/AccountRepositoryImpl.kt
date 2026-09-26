@file:OptIn(ExperimentalCoroutinesApi::class)

package com.a.injector.data.repository

import arrow.core.Either
import com.a.injector.R
import com.a.injector.data.dto.ProfileDto
import com.a.injector.data.mapper.ProfileMapper
import com.a.injector.data.remote.AuthApi
import com.a.injector.data.remote.ProfileApi
import com.a.injector.data.util.TextResource
import com.a.injector.data.util.toMessage
import com.a.injector.domain.model.ProfileModel
import com.a.injector.domain.model.state.AuthResult
import com.a.injector.domain.repository.AccountRepository
import io.github.jan.supabase.auth.user.UserInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import org.koin.core.annotation.Single
import kotlin.time.Duration.Companion.seconds
import kotlin.uuid.Uuid

@Single
class AccountRepositoryImpl(
    private val authApi: AuthApi,
    private val profileApi: ProfileApi
): AccountRepository {
    private val repositoryScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override val currentAuthState: Flow<AuthResult> = authApi.getAuthState().map { userInfo ->
        when {
            userInfo == null -> AuthResult.Unauthenticated
            else -> AuthResult.Authenticated
        }
    }.flowOn(Dispatchers.IO)

    override val currentUserInfo: StateFlow<UserInfo?> =
        authApi.getAuthState().flowOn(Dispatchers.IO).stateIn(
            scope = repositoryScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null
    )

    override val currentProfile: StateFlow<ProfileModel> = authApi.getAuthState().flatMapLatest { userInfo ->
        when (userInfo?.isAnonymous) {
            false -> profileApi.getProfile(userInfo.id).map { profileDto ->
                if (profileDto != null) {
                    ProfileMapper.toModel(profileDto)
                } else {
                    ProfileModel.EMPTY
                }
            }
            true -> flowOf(ProfileModel.GUEST)
            else -> flowOf(ProfileModel.EMPTY)
        }
    }.flowOn(Dispatchers.IO).stateIn(
        scope = repositoryScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = ProfileModel.EMPTY
    )

    override fun signIn(email: String, password: String): Flow<Either<TextResource, Unit>> {
        return flow<Either<TextResource, Unit>> {
            authApi.signIn(
                email = email,
                password = password
            )
            emit(Either.Right(Unit))
        }.catch { throwable ->
            emit(Either.Left(throwable.toMessage()))
        }.flowOn(Dispatchers.IO)
    }

    override fun signUp(email: String, password: String): Flow<Either<TextResource, Unit>> {
        return flow<Either<TextResource, Unit>> {
            authApi.signUp(
                email = email,
                password = password
            )
            delay(1.seconds)
            profileApi.upsertProfile(
                profileDto = ProfileDto(
                    id = authApi.getAuthState().first()!!.id,
                    username = "user${Uuid.random()}"
                )
            )
            emit(Either.Right(Unit))
        }.catch { throwable ->
            emit(Either.Left(throwable.toMessage()))
        }.flowOn(Dispatchers.IO)
    }

    override fun signOtp(email: String): Flow<Either<TextResource, TextResource>> {
        return flow<Either<TextResource, TextResource>> {
            authApi.signOtp(
                email = email
            )
            emit(Either.Right(TextResource.DynamicString("")))
        }.catch { throwable ->
            emit(Either.Left(throwable.toMessage()))
        }.flowOn(Dispatchers.IO)
    }

    override fun verifyOtp(
        email: String,
        otp: String
    ): Flow<Either<TextResource, TextResource>> {
        return flow<Either<TextResource, TextResource>> {
            authApi.verifyOtp(
                email = email,
                otp = otp
            )
            emit(Either.Right(TextResource.DynamicString("")))
        }.catch { throwable ->
            emit(Either.Left(throwable.toMessage()))
        }.flowOn(Dispatchers.IO)
    }

    override fun signGuest(): Flow<Either<TextResource, Unit>> {
        return flow<Either<TextResource, Unit>> {
            authApi.signGuest()
            emit(Either.Right(Unit))
        }.catch { throwable ->
            emit(Either.Left(throwable.toMessage()))
        }.flowOn(Dispatchers.IO)
    }

    override fun signOut(): Flow<Either<TextResource, Unit>> {
        return flow<Either<TextResource, Unit>> {
            authApi.signOut()
            emit(Either.Right(Unit))
        }.catch { throwable ->
            emit(Either.Left(throwable.toMessage()))
        }.flowOn(Dispatchers.IO)
    }

    override fun changePassword(password: String): Flow<Either<TextResource, TextResource>> {
        return flow<Either<TextResource, TextResource>> {
            authApi.changePassword(
                password = password
            )
            emit(Either.Right(TextResource.DynamicString("")))
        }.catch { throwable ->
            emit(Either.Left(throwable.toMessage()))
        }.flowOn(Dispatchers.IO)
    }

    override fun sendResetPassword(email: String): Flow<Either<TextResource, TextResource>> {
        return flow<Either<TextResource, TextResource>> {
            authApi.sendResetPassword(
                email = email
            )
            emit(Either.Right(TextResource.StringResource(R.string.success_password_reset)))
        }.catch { throwable ->
            emit(Either.Left(throwable.toMessage()))
        }.flowOn(Dispatchers.IO)
    }
}