@file:OptIn(ExperimentalCoroutinesApi::class)

package com.a.injector.data.repository

import android.content.Context
import arrow.core.Either
import com.a.injector.R
import com.a.injector.data.dto.ProfileDto
import com.a.injector.data.dto.Role
import com.a.injector.data.mapper.toProfileDto
import com.a.injector.data.mapper.toProfileModel
import com.a.injector.data.mapper.toRequestDetailModel
import com.a.injector.data.mapper.toRequestDto
import com.a.injector.data.remote.AuthApi
import com.a.injector.data.remote.ProfileApi
import com.a.injector.data.remote.RequestApi
import com.a.injector.data.util.toMessage
import com.a.injector.domain.model.ProfileModel
import com.a.injector.domain.model.RequestDetailModel
import com.a.injector.domain.model.RequestModel
import com.a.injector.domain.model.state.AuthResult
import com.a.injector.domain.model.state.RequestState
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
    private val context: Context,
    private val authApi: AuthApi,
    private val profileApi: ProfileApi,
    private val requestApi: RequestApi
): AccountRepository {
    private val repositoryScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override val authState: StateFlow<AuthResult?> = authApi.currentAuth.flatMapLatest { userInfo ->
        val authResult = when {
            userInfo == null -> AuthResult.Unauthenticated
            else -> AuthResult.Authenticated
        }
        flowOf(authResult)
    }.flowOn(Dispatchers.IO).stateIn(
        scope = repositoryScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = null
    )

    override val currentUserInfo: StateFlow<UserInfo?> = authApi.currentAuth.flowOn(Dispatchers.IO).stateIn(
        scope = repositoryScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = null
    )

    override val currentProfile: StateFlow<ProfileModel> = authApi.currentAuth.flatMapLatest { userInfo ->
        when (userInfo?.isAnonymous) {
            false -> profileApi.getProfile(userInfo.id).map { it?.toProfileModel() ?: ProfileModel.EMPTY }
            true -> flowOf(ProfileModel.GUEST)
            else -> flowOf(ProfileModel.EMPTY)
        }
    }.flowOn(Dispatchers.IO).stateIn(
        scope = repositoryScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = ProfileModel.EMPTY
    )

    override fun signIn(email: String, password: String): Flow<Either<String, Unit>> {
        return flow<Either<String, Unit>> {
            authApi.signIn(
                email = email,
                password = password
            )
            emit(Either.Right(Unit))
        }.catch { throwable ->
            emit(Either.Left(throwable.toMessage(context)))
        }.flowOn(Dispatchers.IO)
    }

    override fun signUp(email: String, password: String): Flow<Either<String, Unit>> {
        return flow<Either<String, Unit>> {
            authApi.signUp(
                email = email,
                password = password
            )
            delay(1.seconds)
            profileApi.upsertProfile(
                profileDto = ProfileDto(
                    id = authApi.currentAuth.first()!!.id,
                    username = "user${Uuid.random()}",
                    role = Role.User,
                    contribution = 0
                )
            )
            emit(Either.Right(Unit))
        }.catch { throwable ->
            emit(Either.Left(throwable.toMessage(context)))
        }.flowOn(Dispatchers.IO)
    }

    override fun signGuest(): Flow<Either<String, Unit>> {
        return flow<Either<String, Unit>> {
            authApi.signGuest()
            emit(Either.Right(Unit))
        }.catch { throwable ->
            emit(Either.Left(throwable.toMessage(context)))
        }.flowOn(Dispatchers.IO)
    }

    override fun signOut(): Flow<Either<String, Unit>> {
        return flow<Either<String, Unit>> {
            authApi.signOut()
            emit(Either.Right(Unit))
        }.catch { throwable ->
            emit(Either.Left(throwable.toMessage(context)))
        }.flowOn(Dispatchers.IO)
    }

    override fun sendResetPassword(email: String): Flow<Either<String, String>> {
        return flow<Either<String, String>> {
            authApi.sendResetPassword(
                email = email
            )
            emit(Either.Right(context.getString(R.string.success_password_reset)))
        }.catch { throwable ->
            emit(Either.Left(throwable.toMessage(context)))
        }.flowOn(Dispatchers.IO)
    }

    override fun getProfileByHighestContribution(): Flow<Either<String, List<ProfileModel>>> {
        return profileApi.getProfileByHighestContribution().map { profileDtos ->
            val profileModels = profileDtos.map { it.toProfileModel() }
            Either.Right(profileModels) as Either<String, List<ProfileModel>>
        }.catch { throwable ->
            emit(Either.Left(throwable.toMessage(context)))
        }.flowOn(Dispatchers.IO)
    }

    override fun upsertProfile(profileModel: ProfileModel): Flow<Either<String, String>> {
        return flow<Either<String, String>> {
            profileApi.upsertProfile(
                profileDto = profileModel.toProfileDto()
            )
            emit(Either.Right(context.getString(R.string.success_update_profile)))
        }.catch { throwable ->
            emit(Either.Left(throwable.toMessage(context)))
        }.flowOn(Dispatchers.IO)
    }

    override fun getRequestStatus(): Flow<RequestState> {
        return authApi.currentAuth.flatMapLatest { userInfo ->
            if (userInfo != null) {
                requestApi.getRequestDetail(userInfo.id).map { requestDetailDto ->
                    if (requestDetailDto != null) RequestState.Applied else RequestState.NotApplied
                }
            } else {
                flowOf(RequestState.NotApplied)
            }
        }.flowOn(Dispatchers.IO)
    }

    override fun getRequestDetails(): Flow<Either<String, List<RequestDetailModel>>> {
        return requestApi.getRequestDetails().map { requestDetailDtos ->
            val requestDetailModelDto = requestDetailDtos.map { it.toRequestDetailModel() }
            Either.Right(requestDetailModelDto) as Either<String, List<RequestDetailModel>>
        }.catch { throwable ->
            emit(Either.Left(throwable.toMessage(context)))
        }.flowOn(Dispatchers.IO)
    }

    override fun getGrantedByRole(role: Role): Flow<Either<String, List<ProfileModel>>> {
        return profileApi.getProfilesByRole(role).map { profileDtos ->
            val profileModels = profileDtos.map { it.toProfileModel() }
            Either.Right(profileModels) as Either<String, List<ProfileModel>>
        }.catch { throwable ->
            emit(Either.Left(throwable.toMessage(context)))
        }.flowOn(Dispatchers.IO)
    }

    override fun upsertRequest(requestModel: RequestModel): Flow<Either<String, String>> {
        return flow<Either<String, String>> {
            requestApi.upsertRequest(
                requestDto = requestModel.toRequestDto()
            )
            emit(Either.Right(context.getString(R.string.success_request_role)))
        }.catch { throwable ->
            emit(Either.Left(throwable.toMessage(context)))
        }.flowOn(Dispatchers.IO)
    }

    override fun grantRequest(requestModel: RequestModel): Flow<Either<String, String>> {
        return flow<Either<String, String>> {
            profileApi.upsertRole(
                id = requestModel.id,
                role = requestModel.role
            )
            requestApi.deleteRequest(
                requestDto = requestModel.toRequestDto()
            )
            emit(Either.Left(context.getString(R.string.success_grant_role)))
        }.catch { throwable ->
            emit(Either.Left(throwable.toMessage(context)))
        }.flowOn(Dispatchers.IO)
    }
}