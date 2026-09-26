package com.a.injector.domain.repository

import arrow.core.Either
import com.a.injector.data.util.TextResource
import com.a.injector.domain.model.ProfileModel
import com.a.injector.domain.model.RequestModel
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun getTopSupporter(): Flow<Either<TextResource, List<ProfileModel>>>
    fun getTopContributor(): Flow<Either<TextResource, List<ProfileModel>>>

    fun getRequests(): Flow<Either<TextResource, List<RequestModel>>>
    fun getRequest(profileId: String): Flow<Either<TextResource, RequestModel?>>
    fun grantRequest(requestModel: RequestModel): Either<TextResource, TextResource>
}