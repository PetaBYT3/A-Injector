package com.a.injector.domain.repository

import arrow.core.Either
import com.a.injector.domain.model.SupportModel
import com.a.injector.domain.model.Text
import io.github.vinceglb.filekit.PlatformFile
import kotlinx.coroutines.flow.Flow

interface SupportRepository {
    fun getCurrent(): Flow<Either<Text, SupportModel>>
    fun getSingle(profileId: String): Flow<Either<Text, SupportModel>>
    fun getList(): Flow<Either<Text, List<SupportModel>>>
    fun upsert(supportModel: SupportModel, image: PlatformFile?): Flow<Either<Text, Unit>>
    fun confirm(supportModel: SupportModel): Flow<Either<Text, Unit>>
    fun deny(supportModel: SupportModel): Flow<Either<Text, Unit>>
}