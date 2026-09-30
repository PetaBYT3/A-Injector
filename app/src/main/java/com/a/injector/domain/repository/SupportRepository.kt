package com.a.injector.domain.repository

import arrow.core.Either
import com.a.injector.data.util.TextResource
import com.a.injector.domain.model.SupportModel
import kotlinx.coroutines.flow.Flow

interface SupportRepository {
    fun getCurrent(): Flow<Either<TextResource, SupportModel>>
    fun getSingle(profileId: String): Flow<Either<TextResource, SupportModel>>
    fun getList(): Flow<Either<TextResource, List<SupportModel>>>
    fun upsert(supportModel: SupportModel): Flow<Either<TextResource, Unit>>
    fun confirm(supportModel: SupportModel): Flow<Either<TextResource, Unit>>
    fun deny(supportModel: SupportModel): Flow<Either<TextResource, Unit>>
}