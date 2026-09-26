package com.a.injector.data.repository

import arrow.core.Either
import com.a.injector.R
import com.a.injector.data.mapper.ProfileMapper
import com.a.injector.data.mapper.RequestMapper
import com.a.injector.data.remote.ProfileApi
import com.a.injector.data.remote.RequestApi
import com.a.injector.data.util.TextResource
import com.a.injector.data.util.toMessage
import com.a.injector.domain.model.ProfileModel
import com.a.injector.domain.model.RequestModel
import com.a.injector.domain.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import org.koin.core.annotation.Single

@Single
class UserRepositoryImpl(
    private val profileApi: ProfileApi,
    private val requestApi: RequestApi
): UserRepository {
    override fun getProfiles(): Flow<Either<TextResource, List<ProfileModel>>> {
        return profileApi.getProfiles().map { profileDtos ->
            val profileModels = profileDtos.map { ProfileMapper.toModel(it) }
            Either.Right(profileModels) as Either<TextResource, List<ProfileModel>>
        }.catch { throwable ->
            emit(Either.Left(throwable.toMessage()))
        }.flowOn(Dispatchers.IO)
    }

    override fun getProfile(profileId: String): Flow<Either<TextResource, ProfileModel>> {
        return profileApi.getProfile(
            profileId = profileId
        ).map { profileDto ->
            if (profileDto != null) {
                Either.Right(ProfileMapper.toModel(profileDto)) as Either<TextResource, ProfileModel>
            } else {
                Either.Left(TextResource.StringResource(R.string.exception_no_data))
            }
        }.catch { throwable ->
            emit(Either.Left(throwable.toMessage()))
        }.flowOn(Dispatchers.IO)
    }

    override fun upsertProfile(profileModel: ProfileModel): Flow<Either<TextResource, TextResource>> {
        return flow<Either<TextResource, TextResource>> {
            profileApi.upsertProfile(
                profileDto = ProfileMapper.toDto(profileModel)
            )
            emit(Either.Right(TextResource.StringResource(R.string.success_update_profile)))
        }.catch { throwable ->
            emit(Either.Left(throwable.toMessage()))
        }.flowOn(Dispatchers.IO)
    }

    override fun getTopSupporter(): Flow<Either<TextResource, List<ProfileModel>>> {
        return profileApi.getTopSupporter().map { profileDtos ->
            val profileModels = profileDtos.map { ProfileMapper.toModel(it) }
            Either.Right(profileModels) as Either<TextResource, List<ProfileModel>>
        }.catch { throwable ->
            emit(Either.Left(throwable.toMessage()))
        }.flowOn(Dispatchers.IO)
    }

    override fun getTopContributor(): Flow<Either<TextResource, List<ProfileModel>>> {
        return profileApi.getTopContributor().map { profileDtos ->
            val profileModels = profileDtos.map { ProfileMapper.toModel(it) }
            Either.Right(profileModels) as Either<TextResource, List<ProfileModel>>
        }.catch { throwable ->
            emit(Either.Left(throwable.toMessage()))
        }.flowOn(Dispatchers.IO)
    }

    override fun getRequests(): Flow<Either<TextResource, List<RequestModel>>> {
        return requestApi.getRequests().map { requestDtos ->
            val requestModels = requestDtos.map { RequestMapper.toModel(it) }
            Either.Right(requestModels) as Either<TextResource, List<RequestModel>>
        }.catch { throwable ->
            emit(Either.Left(throwable.toMessage()))
        }.flowOn(Dispatchers.IO)
    }

    override fun getRequest(profileId: String): Flow<Either<TextResource, RequestModel>> {
        return requestApi.getRequest(
            profileId = profileId
        ).map { requestDto ->
            if (requestDto != null) {
                Either.Right(RequestMapper.toModel(requestDto)) as Either<TextResource, RequestModel>
            } else {
                Either.Right(RequestModel.EMPTY)
            }
        }.catch { throwable ->
            emit(Either.Left(throwable.toMessage()))
        }.flowOn(Dispatchers.IO)
    }

    override fun upsertRequest(requestModel: RequestModel): Flow<Either<TextResource, TextResource>> {
        return flow<Either<TextResource, TextResource>> {
            requestApi.upsertRequest(
                requestDto = RequestMapper.toDto(requestModel)
            )
            emit(Either.Right(TextResource.DynamicString("")))
        }.catch { throwable ->
            emit(Either.Left(throwable.toMessage()))
        }.flowOn(Dispatchers.IO)
    }

    override fun applyRequest(requestModel: RequestModel): Flow<Either<TextResource, TextResource>> {
        return flow<Either<TextResource, TextResource>> {
            requestApi.upsertRequest(
                requestDto = RequestMapper.toDto(requestModel)
            )
            emit(Either.Right(TextResource.DynamicString("")))
        }.catch { throwable ->
            emit(Either.Left(throwable.toMessage()))
        }.flowOn(Dispatchers.IO)
    }

    override fun grantRequest(requestModel: RequestModel): Flow<Either<TextResource, TextResource>> {
        return flow<Either<TextResource, TextResource>> {
            profileApi.upsertProfile(
                profileDto = ProfileMapper.toDto(requestModel.profile).copy(
                    role = requestModel.role
                )
            )
            requestApi.deleteRequest(
                requestDto = RequestMapper.toDto(requestModel)
            )
            emit(Either.Right(TextResource.DynamicString("")))
        }.catch { throwable ->
            emit(Either.Left(throwable.toMessage()))
        }.flowOn(Dispatchers.IO)
    }
}