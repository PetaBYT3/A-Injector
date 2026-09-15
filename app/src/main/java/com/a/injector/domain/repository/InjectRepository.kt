package com.a.injector.domain.repository

import arrow.core.Either
import com.a.injector.domain.model.ReplaceModel
import kotlinx.coroutines.flow.Flow

interface InjectRepository {
    fun start(replaceModel: ReplaceModel): Flow<Either<String, String>>
}