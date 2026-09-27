package com.a.injector.data.repository

import arrow.core.Either
import com.a.injector.R
import com.a.injector.data.mapper.ProfileMapper
import com.a.injector.data.mapper.RoleMapper
import com.a.injector.data.mapper.SupportingMapper
import com.a.injector.data.remote.ProfileApi
import com.a.injector.data.remote.RoleApi
import com.a.injector.data.remote.SupportingApi
import com.a.injector.data.util.TextResource
import com.a.injector.data.util.toMessage
import com.a.injector.domain.model.ProfileModel
import com.a.injector.domain.model.RoleModel
import com.a.injector.domain.model.SupportingModel
import com.a.injector.domain.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import org.koin.core.annotation.Single

@Single
class UserRepositoryImpl(
    private val profileApi: ProfileApi,
    private val supportingApi: SupportingApi,
    private val roleApi: RoleApi,
): UserRepository {
    override fun getProfiles(): Flow<Either<TextResource, List<ProfileModel>>> {
        return flow<Either<TextResource, List<ProfileModel>>> {
            profileApi.getList().collect { profileDtos ->
                val profileModels = profileDtos.map { ProfileMapper.toModel(it) }
                Either.Right(profileModels) as Either<TextResource, List<ProfileModel>>
            }
        }.catch { throwable ->
            emit(Either.Left(throwable.toMessage()))
        }.flowOn(Dispatchers.IO)
    }

    override fun getProfile(
        profileId: String
    ): Flow<Either<TextResource, ProfileModel>> {
        return flow<Either<TextResource, ProfileModel>> {
            profileApi.getSingle(
                profileId = profileId
            ).collect { profileDto ->
                if (profileDto != null) {
                    Either.Right(ProfileMapper.toModel(profileDto))
                } else {
                    Either.Left(TextResource.StringResource(R.string.exception_no_data))
                }
            }
        }.catch { throwable ->
            emit(Either.Left(throwable.toMessage()))
        }.flowOn(Dispatchers.IO)
    }

    override fun upsertProfile(profileModel: ProfileModel): Flow<Either<TextResource, TextResource>> {
        return flow<Either<TextResource, TextResource>> {
            profileApi.upsert(
                profileDto = ProfileMapper.toDto(profileModel)
            )
            emit(Either.Right(TextResource.StringResource(R.string.success_update_profile)))
        }.catch { throwable ->
            emit(Either.Left(throwable.toMessage()))
        }.flowOn(Dispatchers.IO)
    }

    override fun getTopSupporter(): Flow<Either<TextResource, List<ProfileModel>>> {
        return profileApi.getListBySupporting().map { profileDtos ->
            val profileModels = profileDtos.map { ProfileMapper.toModel(it) }
            Either.Right(profileModels) as Either<TextResource, List<ProfileModel>>
        }.catch { throwable ->
            emit(Either.Left(throwable.toMessage()))
        }.flowOn(Dispatchers.IO)
    }

    override fun getTopContributor(): Flow<Either<TextResource, List<ProfileModel>>> {
        return profileApi.getListByContributor().map { profileDtos ->
            val profileModels = profileDtos.map { ProfileMapper.toModel(it) }
            Either.Right(profileModels) as Either<TextResource, List<ProfileModel>>
        }.catch { throwable ->
            emit(Either.Left(throwable.toMessage()))
        }.flowOn(Dispatchers.IO)
    }

    override fun getPendingSupportings(): Flow<Either<TextResource, List<SupportingModel>>> {
        return flow<Either<TextResource, List<SupportingModel>>> {
            supportingApi.getList().collect { pendingSupportingDtos ->
                val pendingSupportingModels = pendingSupportingDtos.map { pendingSupportingDto ->
                    SupportingMapper.toModel(pendingSupportingDto)
                }
                Either.Right(pendingSupportingModels)
            }
        }.catch { throwable ->
            emit(Either.Left(throwable.toMessage()))
        }.flowOn(Dispatchers.IO)
    }

    override fun getPendingSupporting(profileId: String): Flow<Either<TextResource, SupportingModel>> {
        return flow<Either<TextResource, SupportingModel>> {
            supportingApi.getSingle(
                profileId = profileId
            ).collect { pendingSupportingDto ->
                if (pendingSupportingDto != null) {
                    Either.Right(SupportingMapper.toModel(pendingSupportingDto))
                } else {
                    Either.Left(TextResource.StringResource(R.string.exception_no_data))
                }
            }
        }.catch { throwable ->
            emit(Either.Left(throwable.toMessage()))
        }.flowOn(Dispatchers.IO)
    }

    override fun confirmPendingSupporting(
        supportingModel: SupportingModel
    ): Flow<Either<TextResource, TextResource>> {
        return flow<Either<TextResource, TextResource>> {
            val currentProfile = profileApi.getSingle(supportingModel.id).first()

            if (currentProfile == null) {
                emit(Either.Left(TextResource.StringResource(R.string.exception_no_data)))
                return@flow
            }

            profileApi.upsert(
                profileDto = currentProfile.copy(
                    nominal = currentProfile.supporting + supportingModel.nominal
                )
            )
            supportingApi.delete(
                supportingDto = SupportingMapper.toDto(supportingModel)
            )

            emit(Either.Right(TextResource.StringResource(R.string.title_success)))
        }.catch { throwable ->
            emit(Either.Left(throwable.toMessage()))
        }.flowOn(Dispatchers.IO)
    }

    override fun denyPendingSupporting(
        supportingModel: SupportingModel
    ): Flow<Either<TextResource, TextResource>> {
        return flow<Either<TextResource, TextResource>> {
            supportingApi.delete(
                supportingDto = SupportingMapper.toDto(supportingModel)
            )
            emit(Either.Right(TextResource.StringResource(R.string.title_success)))
        }.catch { throwable ->
            emit(Either.Left(throwable.toMessage()))
        }.flowOn(Dispatchers.IO)
    }

    override fun getRequests(): Flow<Either<TextResource, List<RoleModel>>> {
        return roleApi.getList().map { requestDtos ->
            val requestModels = requestDtos.map { RoleMapper.toModel(it) }
            Either.Right(requestModels) as Either<TextResource, List<RoleModel>>
        }.catch { throwable ->
            emit(Either.Left(throwable.toMessage()))
        }.flowOn(Dispatchers.IO)
    }

    override fun getRequest(profileId: String): Flow<Either<TextResource, RoleModel>> {
        return roleApi.getSingle(
            profileId = profileId
        ).map { requestDto ->
            if (requestDto != null) {
                Either.Right(RoleMapper.toModel(requestDto)) as Either<TextResource, RoleModel>
            } else {
                Either.Right(RoleModel.EMPTY)
            }
        }.catch { throwable ->
            emit(Either.Left(throwable.toMessage()))
        }.flowOn(Dispatchers.IO)
    }

    override fun upsertRequest(roleModel: RoleModel): Flow<Either<TextResource, TextResource>> {
        return flow<Either<TextResource, TextResource>> {
            roleApi.upsert(
                roleDto = RoleMapper.toDto(roleModel)
            )
            emit(Either.Right(TextResource.DynamicString("")))
        }.catch { throwable ->
            emit(Either.Left(throwable.toMessage()))
        }.flowOn(Dispatchers.IO)
    }

    override fun applyRequest(roleModel: RoleModel): Flow<Either<TextResource, TextResource>> {
        return flow<Either<TextResource, TextResource>> {
            roleApi.upsert(
                roleDto = RoleMapper.toDto(roleModel)
            )
            emit(Either.Right(TextResource.DynamicString("")))
        }.catch { throwable ->
            emit(Either.Left(throwable.toMessage()))
        }.flowOn(Dispatchers.IO)
    }

    override fun grantRequest(roleModel: RoleModel): Flow<Either<TextResource, TextResource>> {
        return flow<Either<TextResource, TextResource>> {
            profileApi.upsert(
                profileDto = ProfileMapper.toDto(roleModel.profile).copy(
                    role = roleModel.role
                )
            )
            roleApi.delete(
                roleDto = RoleMapper.toDto(roleModel)
            )
            emit(Either.Right(TextResource.DynamicString("")))
        }.catch { throwable ->
            emit(Either.Left(throwable.toMessage()))
        }.flowOn(Dispatchers.IO)
    }
}