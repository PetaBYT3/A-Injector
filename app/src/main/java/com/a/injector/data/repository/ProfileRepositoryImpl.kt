@file:OptIn(ExperimentalCoroutinesApi::class)

package com.a.injector.data.repository

import arrow.core.Either
import com.a.injector.R
import com.a.injector.data.mapper.ProfileMapper
import com.a.injector.data.remote.AuthApi
import com.a.injector.data.remote.ProfileApi
import com.a.injector.data.util.TextResource
import com.a.injector.data.util.catchAndDispatch
import com.a.injector.domain.model.ProfileModel
import com.a.injector.domain.repository.ProfileRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import org.koin.core.annotation.Single

@Single
class ProfileRepositoryImpl(
    private val authApi: AuthApi,
    private val profileApi: ProfileApi
): ProfileRepository {
    override fun getCurrent(): Flow<Either<TextResource, ProfileModel>> {
        return flow<Either<TextResource, ProfileModel>> {
            val result = authApi.getAuthState().filterNotNull().flatMapLatest { userInfo ->
                profileApi.getSingle(
                    profileId = userInfo.id
                ).map { profileDto ->
                    if (profileDto != null) {
                        Either.Right(ProfileMapper.toModel(profileDto))
                    } else {
                        Either.Left(TextResource.StringResource(R.string.exception_no_data))
                    }
                }
            }
            emitAll(result)
        }.catchAndDispatch()
    }

    override fun getSingle(profileId: String): Flow<Either<TextResource, ProfileModel>> {
        return flow<Either<TextResource, ProfileModel>> {
            val result = profileApi.getSingle(
                profileId = profileId
            ).map { profileDto ->
                if (profileDto != null) {
                    Either.Right(ProfileMapper.toModel(profileDto))
                } else {
                    Either.Left(TextResource.StringResource(R.string.exception_no_data))
                }
            }
            emitAll(result)
        }.catchAndDispatch()
    }

    override fun getList(): Flow<Either<TextResource, List<ProfileModel>>> {
        return flow<Either<TextResource, List<ProfileModel>>> {
            val result = profileApi.getList().map { profileDtos ->
                val profileModels = profileDtos.map { profileDto ->
                    ProfileMapper.toModel(profileDto)
                }
                Either.Right(profileModels)
            }
            emitAll(result)
        }.catchAndDispatch()
    }

    override fun getListBySupport(): Flow<Either<TextResource, List<ProfileModel>>> {
        return flow<Either<TextResource, List<ProfileModel>>> {
            val result = profileApi.getListBySupporting().map { profileDtos ->
                val profileModels = profileDtos.map { profileDto ->
                    ProfileMapper.toModel(profileDto)
                }
                Either.Right(profileModels)
            }
            emitAll(result)
        }.catchAndDispatch()
    }

    override fun getListByContribution(): Flow<Either<TextResource, List<ProfileModel>>> {
        return flow<Either<TextResource, List<ProfileModel>>> {
            val result = profileApi.getListByContributor().map { profileDtos ->
                val profileModels = profileDtos.map { profileDto ->
                    ProfileMapper.toModel(profileDto)
                }
                Either.Right(profileModels)
            }
            emitAll(result)
        }.catchAndDispatch()
    }

    override fun upsert(profileModel: ProfileModel): Flow<Either<TextResource, Unit>> {
        return flow<Either<TextResource, Unit>> {
            profileApi.upsert(
                profileDto = ProfileMapper.toDto(profileModel)
            )
            emit(Either.Right(Unit))
        }.catchAndDispatch()
    }
}