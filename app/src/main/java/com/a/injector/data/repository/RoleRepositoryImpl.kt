@file:OptIn(ExperimentalCoroutinesApi::class)

package com.a.injector.data.repository

import arrow.core.Either
import com.a.injector.R
import com.a.injector.data.mapper.ProfileMapper
import com.a.injector.data.mapper.RoleMapper
import com.a.injector.data.remote.auth.AuthApi
import com.a.injector.data.remote.profile.ProfileApi
import com.a.injector.data.remote.role.RoleApi
import com.a.injector.data.util.catchAndDispatch
import com.a.injector.domain.model.RoleModel
import com.a.injector.domain.model.Text
import com.a.injector.domain.repository.RoleRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.filterNotNull
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
    override fun getCurrent(): Flow<Either<Text, RoleModel>> {
        return flow<Either<Text, RoleModel>> {
            val result = authApi.getAuthState().filterNotNull().flatMapLatest { userInfo ->
                roleApi.getSingle(
                    profileId = userInfo.id
                ).map { roleDto ->
                    if (roleDto != null) {
                        Either.Right(RoleMapper.toModel(roleDto))
                    } else {
                        Either.Left(Text.Resource(R.string.exception_no_data))
                    }
                }
            }
            emitAll(result)
        }.catchAndDispatch()
    }

    override fun getSingle(profileId: String): Flow<Either<Text, RoleModel>> {
        return flow<Either<Text, RoleModel>> {
            val result = roleApi.getSingle(
                profileId = profileId
            ).map { roleDto ->
                if (roleDto != null) {
                    Either.Right(RoleMapper.toModel(roleDto))
                } else {
                    Either.Left(Text.Resource(R.string.exception_no_data))
                }
            }
            emitAll(result)
        }.catchAndDispatch()
    }

    override fun getList(): Flow<Either<Text, List<RoleModel>>> {
        return flow<Either<Text, List<RoleModel>>> {
            val result = roleApi.getList().map { roleDtos ->
                val profileModels = roleDtos.map { roleDto ->
                    RoleMapper.toModel(roleDto)
                }
                Either.Right(profileModels)
            }
            emitAll(result)
        }.catchAndDispatch()
    }

    override fun upsert(roleModel: RoleModel): Flow<Either<Text, Unit>> {
        return flow<Either<Text, Unit>> {
            roleApi.upsert(
                roleDto = RoleMapper.toDto(roleModel)
            )
            emit(Either.Right(Unit))
        }.catchAndDispatch()
    }

    override fun confirm(roleModel: RoleModel): Flow<Either<Text, Unit>> {
        return flow<Either<Text, Unit>> {
            profileApi.upsert(
                profileDto = ProfileMapper.toDto(roleModel.profile).copy(
                    role = roleModel.role
                )
            )
            roleApi.delete(
                roleDto = RoleMapper.toDto(roleModel)
            )
            emit(Either.Right(Unit))
        }.catchAndDispatch()
    }

    override fun deny(roleModel: RoleModel): Flow<Either<Text, Unit>> {
        return flow<Either<Text, Unit>> {
            roleApi.delete(
                roleDto = RoleMapper.toDto(roleModel)
            )
            emit(Either.Right(Unit))
        }.catchAndDispatch()
    }
}