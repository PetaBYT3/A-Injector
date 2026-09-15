package com.a.injector.data.repository

import android.content.Context
import arrow.core.Either
import com.a.injector.R
import com.a.injector.data.dto.Bucket
import com.a.injector.data.mapper.toHeroDto
import com.a.injector.data.mapper.toHeroModel
import com.a.injector.data.mapper.toHeroWithSkinModel
import com.a.injector.data.mapper.toReplaceDto
import com.a.injector.data.mapper.toReplaceModel
import com.a.injector.data.mapper.toSkinDto
import com.a.injector.data.mapper.toSkinModel
import com.a.injector.data.remote.HeroApi
import com.a.injector.data.remote.ReplaceApi
import com.a.injector.data.remote.SkinApi
import com.a.injector.data.remote.StorageApi
import com.a.injector.data.util.toMessage
import com.a.injector.domain.model.HeroModel
import com.a.injector.domain.model.HeroDetailModel
import com.a.injector.domain.model.ReplaceModel
import com.a.injector.domain.model.SkinModel
import com.a.injector.domain.repository.DatabaseRepository
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.readBytes
import io.github.vinceglb.filekit.size
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import org.koin.core.annotation.Single
import kotlin.time.Clock

@Single
class DatabaseRepositoryImpl(
    private val context: Context,
    private val heroApi: HeroApi,
    private val skinApi: SkinApi,
    private val replaceApi: ReplaceApi,
    private val storageApi: StorageApi
): DatabaseRepository {
    override fun getHeroDetails(): Flow<Either<String, List<HeroDetailModel>>> {
        return heroApi.getHeroDetails().map { heroSkinReplaceDtos ->
            val heroSkinReplaceModels = heroSkinReplaceDtos.map { it.toHeroWithSkinModel() }
            Either.Right(heroSkinReplaceModels) as Either<String, List<HeroDetailModel>>
        }.catch { throwable ->
            emit(Either.Left(throwable.toMessage(context)))
        }.flowOn(Dispatchers.IO)
    }

    override fun getHeroDetail(id: String): Flow<Either<String, HeroDetailModel>> {
        return heroApi.getHeroDetail(id).map { heroSkinReplaceDto ->
            if (heroSkinReplaceDto != null) {
                Either.Right(heroSkinReplaceDto.toHeroWithSkinModel())
            } else {
                Either.Left(context.getString(R.string.message_no_data))
            }
        }.catch { throwable ->
            emit(Either.Left(throwable.toMessage(context)))
        }.flowOn(Dispatchers.IO)
    }

    override fun getHeroes(): Flow<Either<String, List<HeroModel>>> {
        return heroApi.getHeroes().map { heroDto ->
            val heroModels = heroDto.map { it.toHeroModel() }
            Either.Right(heroModels) as Either<String, List<HeroModel>>
        }.catch { throwable ->
            emit(Either.Left(throwable.toMessage(context)))
        }.flowOn(Dispatchers.IO)
    }

    override fun getHero(heroId: String): Flow<Either<String, HeroModel>> {
        return heroApi.getHero(heroId).map { heroDto ->
            if (heroDto != null) {
                Either.Right(heroDto.toHeroModel())
            } else {
                Either.Left(context.getString(R.string.message_no_data))
            }
        }.catch { throwable ->
            emit(Either.Left(throwable.toMessage(context)))
        }.flowOn(Dispatchers.IO)
    }

    override fun upsertHero(heroModel: HeroModel): Flow<Either<String, String>> {
        return flow<Either<String, String>> {
            heroApi.upsertHero(
                hero = heroModel.toHeroDto()
            )
            emit(Either.Right(context.getString(R.string.title_success)))
        }.catch { throwable ->
            emit(Either.Left(throwable.toMessage(context)))
        }.flowOn(Dispatchers.IO)
    }

    override fun deleteHero(heroModel: HeroModel): Flow<Either<String, String>> {
        return flow<Either<String, String>> {
            heroApi.deleteHero(
                hero = heroModel.toHeroDto()
            )
            emit(Either.Right(context.getString(R.string.title_success)))
        }.catch { throwable ->
            emit(Either.Left(throwable.toMessage(context)))
        }.flowOn(Dispatchers.IO)
    }

    override fun getSkin(skinId: String): Flow<Either<String, SkinModel>> {
        return skinApi.getSkin(skinId).map { skinWithReplaceDto ->
            if (skinWithReplaceDto != null) {
                Either.Right(skinWithReplaceDto.toSkinModel())
            } else {
                Either.Left(context.getString(R.string.message_no_data))
            }
        }.catch { throwable ->
            emit(Either.Left(throwable.toMessage(context)))
        }.flowOn(Dispatchers.IO)
    }

    override fun upsertSkin(skinModel: SkinModel): Flow<Either<String, String>> {
        return flow<Either<String, String>> {
            skinApi.upsertSkin(
                skin = skinModel.toSkinDto()
            )
            emit(Either.Right(context.getString(R.string.title_success)))
        }.catch { throwable ->
            emit(Either.Left(throwable.toMessage(context)))
        }.flowOn(Dispatchers.IO)
    }

    override fun deleteSkin(skinModel: SkinModel): Flow<Either<String, String>> {
        return flow<Either<String, String>> {
            skinApi.deleteSkin(
                skin = skinModel.toSkinDto()
            )
            emit(Either.Right(context.getString(R.string.title_success)))
        }.catch { throwable ->
            emit(Either.Left(throwable.toMessage(context)))
        }.flowOn(Dispatchers.IO)
    }

    override fun getReplace(replaceId: String): Flow<Either<String, ReplaceModel>> {
        return replaceApi.getReplace(replaceId).map { replaceDto ->
            if (replaceDto != null) {
                Either.Right(replaceDto.toReplaceModel())
            } else {
                Either.Left(context.getString(R.string.message_no_data))
            }
        }.catch { throwable ->
            emit(Either.Left(throwable.toMessage(context)))
        }.flowOn(Dispatchers.IO)
    }

    override fun upsertReplace(
        replaceModel: ReplaceModel,
        platformFile: PlatformFile?
    ): Flow<Either<String, String>> {
        return flow<Either<String, String>> {
            if (platformFile != null) {
                replaceApi.upsertReplace(
                    replace = replaceModel.toReplaceDto().copy(
                        lastUpdate = Clock.System.now().toEpochMilliseconds(),
                        fileSize = platformFile.size()
                    )
                )
                storageApi.upload(
                    targetBucket = Bucket.SCRIPT,
                    fileByte = platformFile.readBytes(),
                    fileName = "${replaceModel.id}.zip"
                )
            } else {
                replaceApi.upsertReplace(
                    replace = replaceModel.toReplaceDto()
                )
            }
            emit(Either.Right(context.getString(R.string.title_success)))
        }.catch { throwable ->
            emit(Either.Left(throwable.toMessage(context)))
        }.flowOn(Dispatchers.IO)
    }

    override fun deleteReplace(replaceModel: ReplaceModel): Flow<Either<String, String>> {
        return flow<Either<String, String>> {
            replaceApi.deleteReplace(
                replace = replaceModel.toReplaceDto()
            )
            storageApi.delete(
                fromBucket = Bucket.SCRIPT,
                fileName = "${replaceModel.id}.zip"
            )
            emit(Either.Right(context.getString(R.string.title_success)))
        }.catch { throwable ->
            emit(Either.Left(throwable.toMessage(context)))
        }.flowOn(Dispatchers.IO)
    }
}