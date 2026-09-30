@file:OptIn(ExperimentalCoroutinesApi::class)

package com.a.injector.data.repository

import arrow.core.Either
import com.a.injector.R
import com.a.injector.data.mapper.RoleMapper
import com.a.injector.data.remote.AuthApi
import com.a.injector.data.remote.ProfileApi
import com.a.injector.data.remote.RoleApi
import com.a.injector.data.util.TextResource
import com.a.injector.data.util.catchAndDispatch
import com.a.injector.domain.model.RoleModel
import com.a.injector.domain.repository.RoleRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import org.koin.core.annotation.Single

@Single
class RoleRepositoryImpl(
    private val authApi: AuthApi,
    private val profileApi: ProfileApi,
    private val roleApi: RoleApi
): RoleRepository {
    override fun getCurrent(): Flow<Either<TextResource, RoleModel>> {
        return flow<Either<TextResource, RoleModel>> {
            val result = authApi.getAuthState().filterNotNull().flatMapLatest { userInfo ->
                roleApi.getSingle(
                    profileId = userInfo.id
                ).map { roleDto ->
                    if (roleDto != null) {
                        Either.Right(RoleMapper.toModel(roleDto))
                    } else {
                        Either.Left(TextResource.StringResource(R.string.exception_no_data))
                    }
                }
            }
            emitAll(result)
        }.catchAndDispatch()
    }

    override fun getSingle(profileId: String): Flow<Either<TextResource, RoleModel>> {
        return flow<Either<TextResource, RoleModel>> {
            val result = roleApi.getSingle(
                profileId = profileId
            ).map { roleDto ->
                if (roleDto != null) {
                    Either.Right(RoleMapper.toModel(roleDto))
                } else {
                    Either.Left(TextResource.StringResource(R.string.exception_no_data))
                }
            }
            emitAll(result)
        }.catchAndDispatch()
    }

    override fun getList(): Flow<Either<TextResource, List<RoleModel>>> {
        return flow<Either<TextResource, List<RoleModel>>> {
            val result = roleApi.getList().map { roleDtos ->
                val profileModels = roleDtos.map { roleDto ->
                    RoleMapper.toModel(roleDto)
                }
                Either.Right(profileModels)
            }
            emitAll(result)
        }.catchAndDispatch()
    }

    override fun upsert(roleModel: RoleModel): Flow<Either<TextResource, Unit>> {
        return flow<Either<TextResource, Unit>> {
            roleApi.upsert(
                roleDto = RoleMapper.toDto(roleModel)
            )
            emit(Either.Right(Unit))
        }.catchAndDispatch()
    }

    override fun confirm(roleModel: RoleModel): Flow<Either<TextResource, Unit>> {
        return flow<Either<TextResource, Unit>> {
            val profileDto = profileApi.getSingle(
                profileId = roleModel.id
            ).first()
            if (profileDto == null) {
                emit(Either.Left(TextResource.StringResource(R.string.exception_no_data)))
                return@flow
            }
            profileApi.upsert(
                profileDto = profileDto.copy(
                    role = roleModel.role
                )
            )
            roleApi.delete(
                roleDto = RoleMapper.toDto(roleModel)
            )
            emit(Either.Right(Unit))
        }.catchAndDispatch()
    }

    override fun deny(roleModel: RoleModel): Flow<Either<TextResource, Unit>> {
        return flow<Either<TextResource, Unit>> {
            roleApi.delete(
                roleDto = RoleMapper.toDto(roleModel)
            )
            emit(Either.Right(Unit))
        }.catchAndDispatch()
    }
}