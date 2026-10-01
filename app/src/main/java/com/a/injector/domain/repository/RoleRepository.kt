package com.a.injector.domain.repository

import arrow.core.Either
import com.a.injector.domain.model.RoleModel
import com.a.injector.domain.model.Text
import kotlinx.coroutines.flow.Flow

interface RoleRepository {
    fun getCurrent(): Flow<Either<Text, RoleModel>>
    fun getSingle(profileId: String): Flow<Either<Text, RoleModel>>
    fun getList(): Flow<Either<Text, List<RoleModel>>>
    fun upsert(roleModel: RoleModel): Flow<Either<Text, Unit>>
    fun confirm(roleModel: RoleModel): Flow<Either<Text, Unit>>
    fun deny(roleModel: RoleModel): Flow<Either<Text, Unit>>
}