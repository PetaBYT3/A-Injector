package com.a.injector.data.repository

import arrow.core.Either
import com.a.injector.R
import com.a.injector.data.dto.Bucket
import com.a.injector.data.mapper.HeroMapper
import com.a.injector.data.mapper.ReplaceMapper
import com.a.injector.data.mapper.SkinMapper
import com.a.injector.data.remote.AuthApi
import com.a.injector.data.remote.HeroApi
import com.a.injector.data.remote.ProfileApi
import com.a.injector.data.remote.ReplaceApi
import com.a.injector.data.remote.SkinApi
import com.a.injector.data.remote.StorageApi
import com.a.injector.data.util.TextResource
import com.a.injector.data.util.catchAndDispatch
import com.a.injector.domain.model.HeroModel
import com.a.injector.domain.model.ReplaceModel
import com.a.injector.domain.model.SkinModel
import com.a.injector.domain.repository.ScriptRepository
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.readBytes
import io.github.vinceglb.filekit.size
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import org.koin.core.annotation.Single
import kotlin.time.Clock

@Single
class ScriptRepositoryImpl(
    private val authApi: AuthApi,
    private val profileApi: ProfileApi,
    private val heroApi: HeroApi,
    private val skinApi: SkinApi,
    private val replaceApi: ReplaceApi,
    private val storageApi: StorageApi
): ScriptRepository {
    override fun getHeroes(): Flow<Either<TextResource, List<HeroModel>>> {
        return flow<Either<TextResource, List<HeroModel>>> {
            val result = heroApi.getList().map { heroDtos ->
                val heroModels = heroDtos.map { heroDto ->
                    HeroMapper.toModel(heroDto)
                }
                Either.Right(heroModels)
            }
            emitAll(result)
        }.catchAndDispatch()
    }

    override fun getHero(heroId: String): Flow<Either<TextResource, HeroModel>> {
        return flow<Either<TextResource, HeroModel>> {
            val result = heroApi.getSingle(
                heroId = heroId
            ).map { heroDto ->
                if (heroDto != null) {
                    Either.Right(HeroMapper.toModel(heroDto))
                } else {
                    Either.Left(TextResource.StringResource(R.string.exception_no_data))
                }
            }
            emitAll(result)
        }.catchAndDispatch()
    }

    override fun upsertHero(heroModel: HeroModel): Flow<Either<TextResource, Unit>> {
        return flow<Either<TextResource, Unit>> {
            heroApi.upsert(
                hero = HeroMapper.toDto(heroModel)
            )
            emit(Either.Right(Unit))
        }.catchAndDispatch()
    }

    override fun deleteHero(heroModel: HeroModel): Flow<Either<TextResource, Unit>> {
        return flow<Either<TextResource, Unit>> {
            heroApi.delete(
                hero = HeroMapper.toDto(heroModel)
            )
            emit(Either.Right(Unit))
        }.catchAndDispatch()
    }

    override fun getSkin(skinId: String): Flow<Either<TextResource, SkinModel>> {
        return flow<Either<TextResource, SkinModel>> {
            val result = skinApi.getSingle(
                skinId = skinId
            ).map { skinDto ->
                if (skinDto != null) {
                    Either.Right(SkinMapper.toModel(skinDto))
                } else {
                    Either.Left(TextResource.StringResource(R.string.exception_no_data))
                }
            }
            emitAll(result)
        }.catchAndDispatch()
    }

    override fun upsertSkin(skinModel: SkinModel): Flow<Either<TextResource, Unit>> {
        return flow<Either<TextResource, Unit>> {
            skinApi.upsert(
                skin = SkinMapper.toDto(skinModel)
            )
            emit(Either.Right(Unit))
        }.catchAndDispatch()
    }

    override fun deleteSkin(skinModel: SkinModel): Flow<Either<TextResource, Unit>> {
        return flow<Either<TextResource, Unit>> {
            skinApi.delete(
                skin = SkinMapper.toDto(skinModel)
            )
            emit(Either.Right(Unit))
        }.catchAndDispatch()
    }

    override fun getReplace(replaceId: String): Flow<Either<TextResource, ReplaceModel>> {
        return flow<Either<TextResource, ReplaceModel>> {
            val result = replaceApi.getSingle(
                replaceId = replaceId
            ).map { replaceDto ->
                if (replaceDto != null) {
                    Either.Right(ReplaceMapper.toModel(replaceDto))
                } else {
                    Either.Left(TextResource.StringResource(R.string.exception_no_data))
                }
            }
            emitAll(result)
        }.catchAndDispatch()
    }

    override fun upsertReplace(
        replaceModel: ReplaceModel,
        replaceFile: PlatformFile?
    ): Flow<Either<TextResource, Unit>> {
        return flow<Either<TextResource, Unit>> {
            if (replaceFile != null) {
                storageApi.upload(
                    targetBucket = Bucket.SCRIPT,
                    fileByte = replaceFile.readBytes(),
                    fileName = "${replaceModel.id}.zip"
                )
                profileApi.incrementContribution(
                    id = authApi.getAuthState().filterNotNull().first().id
                )
            }

            val lastUpdate = when {
                replaceFile != null -> Clock.System.now().toEpochMilliseconds()
                else -> replaceModel.lastUpdate
            }
            val fileSize = replaceFile?.size() ?: replaceModel.fileSize

            replaceApi.upsert(
                replace = ReplaceMapper.toDto(replaceModel).copy(
                    lastUpdate = lastUpdate,
                    fileSize = fileSize
                )
            )
            emit(Either.Right(Unit))
        }.catchAndDispatch()
    }

    override fun deleteReplace(replaceModel: ReplaceModel): Flow<Either<TextResource, Unit>> {
        return flow<Either<TextResource, Unit>> {
            replaceApi.delete(
                replace = ReplaceMapper.toDto(replaceModel)
            )
            storageApi.delete(
                fromBucket = Bucket.SCRIPT,
                files = listOf("${replaceModel.id}.zip")
            )
            emit(Either.Right(Unit))
        }.catchAndDispatch()
    }
}