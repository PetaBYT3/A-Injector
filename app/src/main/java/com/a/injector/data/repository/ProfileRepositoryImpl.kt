@file:OptIn(ExperimentalCoroutinesApi::class)

package com.a.injector.data.repository

import arrow.core.Either
import com.a.injector.R
import com.a.injector.data.mapper.ProfileMapper
import com.a.injector.data.remote.auth.AuthApi
import com.a.injector.data.remote.profile.ProfileApi
import com.a.injector.data.util.catchAndDispatch
import com.a.injector.domain.model.ProfileModel
import com.a.injector.domain.model.Text
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
    override fun getCurrent(): Flow<Either<Text, ProfileModel>> {
        return flow<Either<Text, ProfileModel>> {
            val result = authApi.getAuthState().filterNotNull().flatMapLatest { userInfo ->
                profileApi.getSingle(
                    profileId = userInfo.id
                ).map { profileDto ->
                    if (profileDto != null) {
                        Either.Right(ProfileMapper.toModel(profileDto))
                    } else {
                        Either.Left(Text.Resource(R.string.exception_no_data))
                    }
                }
            }
            emitAll(result)
        }.catchAndDispatch()
    }

    override fun getSingle(profileId: String): Flow<Either<Text, ProfileModel>> {
        return flow<Either<Text, ProfileModel>> {
            val result = profileApi.getSingle(
                profileId = profileId
            ).map { profileDto ->
                if (profileDto != null) {
                    Either.Right(ProfileMapper.toModel(profileDto))
                } else {
                    Either.Left(Text.Resource(R.string.exception_no_data))
                }
            }
            emitAll(result)
        }.catchAndDispatch()
    }

    override fun getList(): Flow<Either<Text, List<ProfileModel>>> {
        return flow<Either<Text, List<ProfileModel>>> {
            val result = profileApi.getList().map { profileDtos ->
                val profileModels = profileDtos.map { profileDto ->
                    ProfileMapper.toModel(profileDto)
                }
                Either.Right(profileModels)
            }
            emitAll(result)
        }.catchAndDispatch()
    }

    override fun getListBySupport(): Flow<Either<Text, List<ProfileModel>>> {
        return flow<Either<Text, List<ProfileModel>>> {
            val result = profileApi.getListBySupporting().map { profileDtos ->
                val profileModels = profileDtos.map { profileDto ->
                    ProfileMapper.toModel(profileDto)
                }
                Either.Right(profileModels)
            }
            emitAll(result)
        }.catchAndDispatch()
    }

    override fun getListByContribution(): Flow<Either<Text, List<ProfileModel>>> {
        return flow<Either<Text, List<ProfileModel>>> {
            val result = profileApi.getListByContributor().map { profileDtos ->
                val profileModels = profileDtos.map { profileDto ->
                    ProfileMapper.toModel(profileDto)
                }
                Either.Right(profileModels)
            }
            emitAll(result)
        }.catchAndDispatch()
    }

    override fun upsert(profileModel: ProfileModel): Flow<Either<Text, Unit>> {
        return flow<Either<Text, Unit>> {
            profileApi.upsert(
                profileDto = ProfileMapper.toDto(profileModel)
            )
            emit(Either.Right(Unit))
        }.catchAndDispatch()
    }
}