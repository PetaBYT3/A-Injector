@file:OptIn(ExperimentalCoroutinesApi::class)

package com.a.injector.data.repository

import arrow.core.Either
import com.a.injector.R
import com.a.injector.data.mapper.ProfileMapper
import com.a.injector.data.mapper.SupportMapper
import com.a.injector.data.remote.auth.AuthApi
import com.a.injector.data.remote.profile.ProfileApi
import com.a.injector.data.remote.storage.StorageApi
import com.a.injector.data.remote.support.SupportApi
import com.a.injector.data.util.catchAndDispatch
import com.a.injector.data.util.toWebpByteArray
import com.a.injector.domain.model.SupportModel
import com.a.injector.domain.model.Text
import com.a.injector.domain.model.state.Bucket
import com.a.injector.domain.repository.SupportRepository
import io.github.vinceglb.filekit.PlatformFile
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import org.koin.core.annotation.Single

@Single
class SupportRepositoryImpl(
    private val authApi: AuthApi,
    private val profileApi: ProfileApi,
    private val supportApi: SupportApi,
    private val storageApi: StorageApi
): SupportRepository {
    override fun getCurrent(): Flow<Either<Text, SupportModel>> {
        return flow<Either<Text, SupportModel>> {
            val result = authApi.getAuthState().filterNotNull().flatMapLatest { userInfo ->
                supportApi.getSingle(
                    profileId = userInfo.id
                ).map { supportDto ->
                    if (supportDto != null) {
                        Either.Right(SupportMapper.toModel(supportDto))
                    } else {
                        Either.Left(Text.Resource(R.string.exception_no_data))
                    }
                }
            }
            emitAll(result)
        }.catchAndDispatch()
    }

    override fun getSingle(profileId: String): Flow<Either<Text, SupportModel>> {
        return flow<Either<Text, SupportModel>> {
            val result = supportApi.getSingle(
                profileId = profileId
            ).map { supportDto ->
                if (supportDto != null) {
                    Either.Right(SupportMapper.toModel(supportDto))
                } else {
                    Either.Left(Text.Resource(R.string.exception_no_data))
                }
            }
            emitAll(result)
        }.catchAndDispatch()
    }

    override fun getList(): Flow<Either<Text, List<SupportModel>>> {
        return flow<Either<Text, List<SupportModel>>> {
            val result = supportApi.getList().map { supportDtos ->
                val supportModels = supportDtos.map { supportDto ->
                    SupportMapper.toModel(supportDto)
                }
                Either.Right(supportModels)
            }
            emitAll(result)
        }.catchAndDispatch()
    }

    override fun upsert(
        supportModel: SupportModel,
        image: PlatformFile?
    ): Flow<Either<Text, Unit>> {
        return flow<Either<Text, Unit>> {
            if (image == null) {
                emit(Either.Left(Text.Resource(R.string.exception_no_image)))
                return@flow
            }

            val imageUrl = storageApi.upload(
                targetBucket = Bucket.IMAGE,
                fileByte = image.toWebpByteArray(),
                fileName = "${supportModel.id}.webp"
            )
            supportApi.upsert(
                supportDto = SupportMapper.toDto(supportModel).copy(
                    imageUrl = imageUrl
                )
            )
            emit(Either.Right(Unit))
        }.catchAndDispatch()
    }

    override fun confirm(supportModel: SupportModel): Flow<Either<Text, Unit>> {
        return flow<Either<Text, Unit>> {
            profileApi.upsert(
                profileDto = ProfileMapper.toDto(supportModel.profile).copy(
                    support = supportModel.profile.support + supportModel.support
                )
            )
            supportApi.delete(
                supportDto = SupportMapper.toDto(supportModel)
            )
            storageApi.delete(
                fromBucket = Bucket.IMAGE,
                files = listOf("${supportModel.id}.webp")
            )
            emit(Either.Right(Unit))
        }.catchAndDispatch()
    }

    override fun deny(supportModel: SupportModel): Flow<Either<Text, Unit>> {
        return flow {
            supportApi.delete(
                supportDto = SupportMapper.toDto(supportModel)
            )
            storageApi.delete(
                fromBucket = Bucket.IMAGE,
                files = listOf("${supportModel.id}.webp")
            )
            emit(Either.Right(Unit))
        }.catchAndDispatch()
    }
}