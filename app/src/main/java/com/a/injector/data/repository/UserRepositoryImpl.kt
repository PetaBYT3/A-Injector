package com.a.injector.data.repository

import arrow.core.Either
import com.a.injector.data.util.TextResource
import com.a.injector.domain.model.ProfileModel
import com.a.injector.domain.model.RequestModel
import com.a.injector.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import org.koin.core.annotation.Single

@Single
class UserRepositoryImpl(

): UserRepository {
    override fun getTopSupporter(): Flow<Either<TextResource, List<ProfileModel>>> {
        TODO("Not yet implemented")
    }

    override fun getTopContributor(): Flow<Either<TextResource, List<ProfileModel>>> {
        TODO("Not yet implemented")
    }

    override fun getRequests(): Flow<Either<TextResource, List<RequestModel>>> {
        TODO("Not yet implemented")
    }

    override fun getRequest(profileId: String): Flow<Either<TextResource, RequestModel?>> {
        TODO("Not yet implemented")
    }

    override fun grantRequest(requestModel: RequestModel): Either<TextResource, TextResource> {
        TODO("Not yet implemented")
    }
}