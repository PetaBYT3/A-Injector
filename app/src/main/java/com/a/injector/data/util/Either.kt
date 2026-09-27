package com.a.injector.data.util

import arrow.core.Either

fun <L, R> R.asRight(): Either<L, R> = Either.Right(this)
fun <L, R> L.asLeft(): Either<L, R> = Either.Left(this)