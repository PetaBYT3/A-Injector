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
import com.a.injector.data.util.toMessage
import com.a.injector.domain.model.HeroModel
import com.a.injector.domain.model.ReplaceModel
import com.a.injector.domain.model.SkinModel
import com.a.injector.domain.repository.ScriptRepository
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.readBytes
import io.github.vinceglb.filekit.size
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
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
        return heroApi.getHeroes().map { heroDtos ->
            val heroModels = heroDtos.map { HeroMapper.toModel(it) }
            Either.Right(heroModels) as Either<TextResource, List<HeroModel>>
        }.catch { throwable ->
            emit(Either.Left(throwable.toMessage()))
        }.flowOn(Dispatchers.IO)
    }

    override fun getHero(heroId: String): Flow<Either<TextResource, HeroModel>> {
        return heroApi.getHero(
            heroId = heroId
        ).map { heroDto ->
            if (heroDto != null) {
                Either.Right(HeroMapper.toModel(heroDto)) as Either<TextResource, HeroModel>
            } else {
                Either.Left(TextResource.StringResource(R.string.exception_no_data))
            }
        }.catch { throwable ->
            emit(Either.Left(throwable.toMessage()))
        }.flowOn(Dispatchers.IO)
    }

    override fun upsertHero(heroModel: HeroModel): Flow<Either<TextResource, Unit>> {
        return flow<Either<TextResource, Unit>> {
            heroApi.upsertHero(
                hero = HeroMapper.toDto(heroModel)
            )
            emit(Either.Right(Unit))
        }.catch { throwable ->
            emit(Either.Left(throwable.toMessage()))
        }.flowOn(Dispatchers.IO)
    }

    override fun deleteHero(heroModel: HeroModel): Flow<Either<TextResource, Unit>> {
        return flow<Either<TextResource, Unit>> {
            heroApi.deleteHero(
                hero = HeroMapper.toDto(heroModel)
            )
            emit(Either.Right(Unit))
        }.catch { throwable ->
            emit(Either.Left(throwable.toMessage()))
        }.flowOn(Dispatchers.IO)
    }

    override fun getSkin(skinId: String): Flow<Either<TextResource, SkinModel>> {
        return skinApi.getSkin(
            skinId = skinId
        ).map { skinDto ->
            if (skinDto != null) {
                Either.Right(SkinMapper.toModel(skinDto)) as Either<TextResource, SkinModel>
            } else {
                Either.Left(TextResource.StringResource(R.string.exception_no_data))
            }
        }.catch { throwable ->
            emit(Either.Left(throwable.toMessage()))
        }.flowOn(Dispatchers.IO)
    }

    override fun upsertSkin(skinModel: SkinModel): Flow<Either<TextResource, Unit>> {
        return flow<Either<TextResource, Unit>> {
            skinApi.upsertSkin(
                skin = SkinMapper.toDto(skinModel)
            )
            emit(Either.Right(Unit))
        }.catch { throwable ->
            emit(Either.Left(throwable.toMessage()))
        }.flowOn(Dispatchers.IO)
    }

    override fun deleteSkin(skinModel: SkinModel): Flow<Either<TextResource, Unit>> {
        return flow<Either<TextResource, Unit>> {
            skinApi.deleteSkin(
                skin = SkinMapper.toDto(skinModel)
            )
            emit(Either.Right(Unit))
        }.catch { throwable ->
            emit(Either.Left(throwable.toMessage()))
        }.flowOn(Dispatchers.IO)
    }

    override fun getReplace(replaceId: String): Flow<Either<TextResource, ReplaceModel>> {
        return replaceApi.getReplace(
            replaceId = replaceId
        ).map { replaceDto ->
            if (replaceDto != null) {
                Either.Right(ReplaceMapper.toModel(replaceDto)) as Either<TextResource, ReplaceModel>
            } else {
                Either.Left(TextResource.StringResource(R.string.exception_no_data))
            }
        }.catch { throwable ->
            emit(Either.Left(throwable.toMessage()))
        }.flowOn(Dispatchers.IO)
    }

    override fun upsertReplace(
        replaceModel: ReplaceModel,
        replaceFile: PlatformFile?
    ): Flow<Either<TextResource, Unit>> {
        return flow<Either<TextResource, Unit>> {
            if (replaceFile != null) {
                replaceApi.upsertReplace(
                    replace = ReplaceMapper.toDto(replaceModel).copy(
                        lastUpdate = Clock.System.now().toEpochMilliseconds(),
                        fileSize = replaceFile.size()
                    )
                )
                storageApi.upload(
                    targetBucket = Bucket.SCRIPT,
                    fileByte = replaceFile.readBytes(),
                    fileName = "${replaceModel.id}.zip"
                )
                profileApi.incrementContribution(
                    id = authApi.getAuthState().first()?.id!!
                )
            } else {
                replaceApi.upsertReplace(
                    replace = ReplaceMapper.toDto(replaceModel)
                )
            }
            emit(Either.Right(Unit))
        }.catch { throwable ->
            emit(Either.Left(throwable.toMessage()))
        }.flowOn(Dispatchers.IO)
    }

    override fun deleteReplace(replaceModel: ReplaceModel): Flow<Either<TextResource, Unit>> {
        return flow<Either<TextResource, Unit>> {
            replaceApi.deleteReplace(
                replace = ReplaceMapper.toDto(replaceModel)
            )
            storageApi.delete(
                fromBucket = Bucket.SCRIPT,
                files = listOf("${replaceModel.id}.zip")
            )
            emit(Either.Right(Unit))
        }.catch { throwable ->
            emit(Either.Left(throwable.toMessage()))
        }.flowOn(Dispatchers.IO)
    }
}