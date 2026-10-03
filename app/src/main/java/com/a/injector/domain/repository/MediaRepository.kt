package com.a.injector.domain.repository

import arrow.core.Either
import com.a.injector.domain.model.Text
import kotlinx.coroutines.flow.Flow

interface MediaRepository {
    fun downloadDrawable(drawable: Int, fileName: String): Flow<Either<Text, Text>>
}