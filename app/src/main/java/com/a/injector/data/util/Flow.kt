package com.a.injector.data.util

import arrow.core.Either
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn

fun <T> Flow<Either<TextResource, T>>.catchAndDispatch(
    dispatcher: CoroutineDispatcher = Dispatchers.IO
): Flow<Either<TextResource, T>> {
    return this.catch { throwable ->
        emit(Either.Left(throwable.toMessage()))
    }.flowOn(dispatcher)
}