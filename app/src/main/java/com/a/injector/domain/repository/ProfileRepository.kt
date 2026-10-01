package com.a.injector.domain.repository

import arrow.core.Either
import com.a.injector.domain.model.ProfileModel
import com.a.injector.domain.model.Text
import kotlinx.coroutines.flow.Flow

interface ProfileRepository {
    fun getCurrent(): Flow<Either<Text, ProfileModel>>
    fun getSingle(profileId: String): Flow<Either<Text, ProfileModel>>
    fun getList(): Flow<Either<Text, List<ProfileModel>>>
    fun getListBySupport(): Flow<Either<Text, List<ProfileModel>>>
    fun getListByContribution(): Flow<Either<Text, List<ProfileModel>>>
    fun upsert(profileModel: ProfileModel): Flow<Either<Text, Unit>>
}