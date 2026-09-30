package com.a.injector.domain.repository

import arrow.core.Either
import com.a.injector.data.util.TextResource
import com.a.injector.domain.model.RoleModel
import kotlinx.coroutines.flow.Flow

interface RoleRepository {
    fun getCurrent(): Flow<Either<TextResource, RoleModel>>
    fun getSingle(profileId: String): Flow<Either<TextResource, RoleModel>>
    fun getList(): Flow<Either<TextResource, List<RoleModel>>>
    fun upsert(roleModel: RoleModel): Flow<Either<TextResource, Unit>>
    fun confirm(roleModel: RoleModel): Flow<Either<TextResource, Unit>>
    fun deny(roleModel: RoleModel): Flow<Either<TextResource, Unit>>
}