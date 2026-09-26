package com.a.injector.data.repository

import arrow.core.Either
import com.a.injector.R
import com.a.injector.data.dto.Bucket
import com.a.injector.data.remote.ReplaceApi
import com.a.injector.data.remote.StorageApi
import com.a.injector.data.util.TextResource
import com.a.injector.data.util.toMessage
import com.a.injector.domain.repository.OptimizeDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import org.koin.core.annotation.Single

@Single
class OptimizeDatabaseImpl(
    private val replaceApi: ReplaceApi,
    private val storageApi: StorageApi
): OptimizeDatabase {
    override fun cleanStorage(): Flow<Either<TextResource, TextResource>> {
        return flow<Either<TextResource, TextResource>> {
            val filesInPostgrest = replaceApi.getReplaces().first().map { "${it.id}.zip" }
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