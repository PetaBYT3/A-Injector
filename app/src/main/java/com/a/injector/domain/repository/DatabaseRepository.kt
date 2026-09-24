package com.a.injector.domain.repository

import arrow.core.Either
import com.a.injector.data.util.TextResource
import com.a.injector.domain.model.HeroDetailModel
import com.a.injector.domain.model.HeroModel
import com.a.injector.domain.model.ReplaceModel
import com.a.injector.domain.model.SkinModel
import com.a.injector.domain.model.VersionModel
import io.github.vinceglb.filekit.PlatformFile
import kotlinx.coroutines.flow.Flow

interface DatabaseRepository {
    fun getVersion(): Flow<Either<TextResource, VersionModel>>

    fun getHeroDetails(): Flow<Either<TextResource, List<HeroDetailModel>>>
    fun getHeroDetail(id: String): Flow<Either<TextResource, HeroDetailModel>>
    fun getHeroes(): Flow<Either<TextResource, List<HeroModel>>>
    fun getHero(heroId: String): Flow<Either<TextResource, HeroModel>>
    fun upsertHero(heroModel: HeroModel): Flow<Either<TextResource, TextResource>>
    fun deleteHero(heroModel: HeroModel): Flow<Either<TextResource, TextResource>>

    fun getSkin(skinId: String): Flow<Either<TextResource, SkinModel>>
    fun upsertSkin(skinModel: SkinModel): Flow<Either<TextResource, TextResource>>
    fun deleteSkin(skinModel: SkinModel): Flow<Either<TextResource, TextResource>>

    fun getReplace(replaceId: String): Flow<Either<TextResource, ReplaceModel>>
    fun upsertReplace(
        replaceModel: ReplaceModel,
        platformFile: PlatformFile?
    ): Flow<Either<TextResource, TextResource>>
    fun deleteReplace(replaceModel: ReplaceModel): Flow<Either<TextResource, TextResource>>

    fun cleanStorage(): Flow<Either<TextResource, TextResource>>
}