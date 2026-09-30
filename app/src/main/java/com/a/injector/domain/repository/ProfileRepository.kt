package com.a.injector.domain.repository

import arrow.core.Either
import com.a.injector.data.util.TextResource
import com.a.injector.domain.model.ProfileModel
import kotlinx.coroutines.flow.Flow

interface ProfileRepository {
    fun getCurrent(): Flow<Either<TextResource, ProfileModel>>
    fun getListBySupport(): Flow<Either<TextResource, List<ProfileModel>>>
    fun getListByContribution(): Flow<Either<TextResource, List<ProfileModel>>>
    fun upsert(profileModel: ProfileModel): Flow<Either<TextResource, Unit>>
}