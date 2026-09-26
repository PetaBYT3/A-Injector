package com.a.injector.domain.repository

import arrow.core.Either
import com.a.injector.data.util.TextResource
import com.a.injector.domain.model.HeroModel
import com.a.injector.domain.model.ReplaceModel
import com.a.injector.domain.model.SkinModel
import io.github.vinceglb.filekit.PlatformFile
import kotlinx.coroutines.flow.Flow

interface ScriptRepository {
    fun getHeroes(): Flow<Either<TextResource, List<HeroModel>>>
    fun getHero(heroId: String): Flow<Either<TextResource, HeroModel>>
    fun upsertHero(heroModel: HeroModel): Flow<Either<TextResource, Unit>>
    fun deleteHero(heroModel: HeroModel): Flow<Either<TextResource, Unit>>

    fun getSkin(skinId: String): Flow<Either<TextResource, SkinModel>>
    fun upsertSkin(skinModel: SkinModel): Flow<Either<TextResource, Unit>>
    fun deleteSkin(skinModel: SkinModel): Flow<Either<TextResource, Unit>>

    fun getReplace(replaceId: String): Flow<Either<TextResource, ReplaceModel>>
    fun upsertReplace(
        replaceModel: ReplaceModel,
        replaceFile: PlatformFile?
    ): Flow<Either<TextResource, Unit>>
    fun deleteReplace(replaceModel: ReplaceModel): Flow<Either<TextResource, Unit>>
}