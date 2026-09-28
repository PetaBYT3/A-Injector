package com.a.injector.data.repository

import arrow.core.Either
import com.a.injector.R
import com.a.injector.data.dto.Bucket
import com.a.injector.data.remote.ReplaceApi
import com.a.injector.data.remote.StorageApi
import com.a.injector.data.util.TextResource
import com.a.injector.data.util.toMessage
import com.a.injector.domain.repository.OptimizeDatabaseRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import org.koin.core.annotation.Single

@Single
class OptimizeDatabaseRepositoryImpl(
    private val replaceApi: ReplaceApi,
    private val storageApi: StorageApi
): OptimizeDatabaseRepository {
    override fun getFileUrl(
        bucket: Bucket,
        fileName: String
    ): Flow<Either<TextResource, String>> {
        return flow<Either<TextResource, String>> {
            val url = storageApi.getFileUrl(
                fromBucket = bucket,
                fileName = fileName
            )
            emit(Either.Right(url))
        }.catch { throwable ->
            emit(Either.Left(throwable.toMessage()))
        }.flowOn(Dispatchers.IO)
    }

    override fun cleanStorage(): Flow<Either<TextResource, TextResource>> {
        return flow<Either<TextResource, TextResource>> {
            val filesInPostgrest = replaceApi.getList().first().map { "${it.id}.zip" }
            val filesInStorage = storageApi.getFileNames(Bucket.SCRIPT)
            val filesToDelete = filesInStorage.filter { it !in filesInPostgrest }

            if (filesToDelete.isNotEmpty()) {
                storageApi.delete(
                    fromBucket = Bucket.SCRIPT,
                    files = filesToDelete
                )
            }
            emit(Either.Right(TextResource.StringResource(R.string.title_success)))
        }.catch { throwable ->
            emit(Either.Left(throwable.toMessage()))
        }.flowOn(Dispatchers.IO)
    }
}