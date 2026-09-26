package com.a.injector.domain.repository

import arrow.core.Either
import com.a.injector.data.util.TextResource
import kotlinx.coroutines.flow.Flow

interface OptimizeDatabaseRepository {
    fun cleanStorage(): Flow<Either<TextResource, TextResource>>
}