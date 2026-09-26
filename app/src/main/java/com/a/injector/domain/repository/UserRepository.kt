package com.a.injector.domain.repository

import arrow.core.Either
import com.a.injector.data.util.TextResource
import com.a.injector.domain.model.ProfileModel
import com.a.injector.domain.model.RequestModel
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun getProfiles(): Flow<Either<TextResource, List<ProfileModel>>>
    fun getProfile(profileId: String): Flow<Either<TextResource, ProfileModel>>
    fun upsertProfile(profileModel: ProfileModel): Flow<Either<TextResource, TextResource>>

    fun getTopSupporter(): Flow<Either<TextResource, List<ProfileModel>>>
    fun getTopContributor(): Flow<Either<TextResource, List<ProfileModel>>>

    fun getRequests(): Flow<Either<TextResource, List<RequestModel>>>
    fun getRequest(profileId: String): Flow<Either<TextResource, RequestModel>>
    fun upsertRequest(requestModel: RequestModel): Flow<Either<TextResource, TextResource>>
    fun applyRequest(requestModel: RequestModel): Flow<Either<TextResource, TextResource>>
    fun grantRequest(requestModel: RequestModel): Flow<Either<TextResource, TextResource>>
}