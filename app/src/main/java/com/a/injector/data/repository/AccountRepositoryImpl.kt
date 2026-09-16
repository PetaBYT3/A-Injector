@file:OptIn(ExperimentalCoroutinesApi::class)

package com.a.injector.data.repository

import android.content.Context
import arrow.core.Either
import com.a.injector.R
import com.a.injector.data.dto.Role
import com.a.injector.data.mapper.toProfileDto
import com.a.injector.data.mapper.toProfileModel
import com.a.injector.data.mapper.toRequestDetailModel
import com.a.injector.data.mapper.toRequestDto
import com.a.injector.data.remote.AuthApi
import com.a.injector.data.remote.ProfileApi
import com.a.injector.data.remote.RequestApi
import com.a.injector.data.util.toMessage
import com.a.injector.domain.model.AuthState
import com.a.injector.domain.model.ProfileModel
import com.a.injector.domain.model.RequestDetailModel
import com.a.injector.domain.model.RequestModel
import com.a.injector.domain.repository.AccountRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import org.koin.core.annotation.Single

@Single
class AccountRepositoryImpl(
    private val context: Context,
    private val authApi: AuthApi,
    private val profileApi: ProfileApi,
    private val requestApi: RequestApi
): AccountRepository {
    override val currentAuth: Flow<AuthState> = authApi.currentAuth.flatMapLatest { userInfo ->
        when {
            userInfo == null -> flowOf(AuthState.Unauthorized)
            userInfo.isAnonymous == true -> flowOf(AuthState.Guest)
            else -> {
                profileApi.getProfile(userInfo.id).map { profileDto ->
                    AuthState.Authorized(
                        userInfo = userInfo,
                        profileModel = profileDto?.toProfileModel() ?: ProfileModel.EMPTY
                    )
                }
            }
        }
    }

    override fun signIn(email: String, password: String): Flow<Either<String, Unit>> {
        return flow<Either<String, Unit>> {
            authApi.signIn(
                email = email,
                password = password
            )
            emit(Either.Right(Unit))
            return@flow
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
            emit(Either.Right(Unit))
            return@flow
        }.catch { throwable ->
            emit(Either.Left(throwable.toMessage(context)))
        }.flowOn(Dispatchers.IO)
    }

    override fun signGuest(): Flow<Either<String, Unit>> {
        return flow<Either<String, Unit>> {
            authApi.signGuest()
            emit(Either.Right(Unit))
            return@flow
        }.catch { throwable ->
            emit(Either.Left(throwable.toMessage(context)))
        }.flowOn(Dispatchers.IO)
    }

    override fun signOut(): Flow<Either<String, Unit>> {
        return flow<Either<String, Unit>> {
            authApi.signOut()
            emit(Either.Right(Unit))
            return@flow
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
            emit(Either.Right(context.getString(R.string.title_success)))
        }.catch { throwable ->
            emit(Either.Left(throwable.toMessage(context)))
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
            emit(Either.Right(context.getString(R.string.title_success)))
        }.catch { throwable ->
            emit(Either.Left(throwable.toMessage(context)))
        }.flowOn(Dispatchers.IO)
    }

    override fun grantRequest(requestModel: RequestModel): Flow<Either<String, String>> {
        return flow<Either<String, String>> {
            profileApi.upsertRole(
                id = requestModel.profileId,
                role = requestModel.role
            )
            requestApi.deleteRequest(
                requestDto = requestModel.toRequestDto()
            )
            emit(Either.Left(context.getString(R.string.title_success)))
        }.catch { throwable ->
            emit(Either.Left(throwable.toMessage(context)))
        }.flowOn(Dispatchers.IO)
    }
}