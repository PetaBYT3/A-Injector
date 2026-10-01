package com.a.injector.domain.repository

import arrow.core.Either
import com.a.injector.domain.model.Text
import com.a.injector.domain.model.state.Bucket
import kotlinx.coroutines.flow.Flow

interface OptimizeDatabaseRepository {
    fun getFileUrl(bucket: Bucket, fileName: String): Flow<Either<Text, String>>
    fun cleanStorage(): Flow<Either<Text, Text>>
}