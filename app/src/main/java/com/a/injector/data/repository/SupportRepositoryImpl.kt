@file:OptIn(ExperimentalCoroutinesApi::class)

package com.a.injector.data.repository

import arrow.core.Either
import com.a.injector.R
import com.a.injector.data.mapper.SupportMapper
import com.a.injector.data.remote.AuthApi
import com.a.injector.data.remote.ProfileApi
import com.a.injector.data.remote.SupportingApi
import com.a.injector.data.util.TextResource
import com.a.injector.data.util.catchAndDispatch
import com.a.injector.domain.model.SupportModel
import com.a.injector.domain.repository.SupportRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import org.koin.core.annotation.Single

@Single
class SupportRepositoryImpl(
    private val authApi: AuthApi,
    private val profileApi: ProfileApi,
    private val supportingApi: SupportingApi
): SupportRepository {
    override fun getCurrent(): Flow<Either<TextResource, SupportModel>> {
        return flow<Either<TextResource, SupportModel>> {
            val result = authApi.getAuthState().filterNotNull().flatMapLatest { userInfo ->
                supportingApi.getSingle(
                    profileId = userInfo.id
                ).map { supportDto ->
                    if (supportDto != null) {
                        Either.Right(SupportMapper.toModel(supportDto))
                    } else {
                        Either.Left(TextResource.StringResource(R.string.exception_no_data))
                    }
                }
            }
            emitAll(result)
        }.catchAndDispatch()
    }

    override fun getSingle(profileId: String): Flow<Either<TextResource, SupportModel>> {
        return flow<Either<TextResource, SupportModel>> {
            val result = supportingApi.getSingle(
                profileId = profileId
            ).map { supportDto ->
                if (supportDto != null) {
                    Either.Right(SupportMapper.toModel(supportDto))
                } else {
                    Either.Left(TextResource.StringResource(R.string.exception_no_data))
                }
            }
            emitAll(result)
        }.catchAndDispatch()
    }

    override fun getList(): Flow<Either<TextResource, List<SupportModel>>> {
        return flow<Either<TextResource, List<SupportModel>>> {
            val result = supportingApi.getList().map { supportDtos ->
                val supportModels = supportDtos.map { supportDto ->
                    SupportMapper.toModel(supportDto)
                }
                Either.Right(supportModels)
            }
            emitAll(result)
        }.catchAndDispatch()
    }

    override fun upsert(supportModel: SupportModel): Flow<Either<TextResource, Unit>> {
        return flow<Either<TextResource, Unit>> {
            supportingApi.upsert(
                supportDto = SupportMapper.toDto(supportModel)
            )
            emit(Either.Right(Unit))
        }.catchAndDispatch()
    }

    override fun confirm(supportModel: SupportModel): Flow<Either<TextResource, Unit>> {
        return flow<Either<TextResource, Unit>> {
            val profileDto = profileApi.getSingle(
                profileId = supportModel.id
            ).first()

            if (profileDto == null) {
                emit(Either.Left(TextResource.StringResource(R.string.exception_no_data)))
                return@flow
            }

            profileApi.upsert(
                profileDto = profileDto.copy(
                    support = profileDto.support + supportModel.nominal
                )
            )
            supportingApi.delete(
                supportDto = SupportMapper.toDto(supportModel)
            )
            emit(Either.Right(Unit))
        }.catchAndDispatch()
    }

    override fun deny(supportModel: SupportModel): Flow<Either<TextResource, Unit>> {
        return flow {
            supportingApi.delete(
                supportDto = SupportMapper.toDto(supportModel)
            )
            emit(Either.Right(Unit))
        }.catchAndDispatch()
    }
}