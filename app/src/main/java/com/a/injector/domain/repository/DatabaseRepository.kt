package com.a.injector.domain.repository

import arrow.core.Either
import com.a.injector.domain.model.HeroDetailModel
import com.a.injector.domain.model.HeroModel
import com.a.injector.domain.model.ReplaceModel
import com.a.injector.domain.model.SkinModel
import io.github.vinceglb.filekit.PlatformFile
import kotlinx.coroutines.flow.Flow

interface DatabaseRepository {
    fun getHeroDetails(): Flow<Either<String, List<HeroDetailModel>>>
    fun getHeroDetail(id: String): Flow<Either<String, HeroDetailModel>>
    fun getHeroes(): Flow<Either<String, List<HeroModel>>>
    fun getHero(heroId: String): Flow<Either<String, HeroModel>>
    fun upsertHero(heroModel: HeroModel): Flow<Either<String, String>>
    fun deleteHero(heroModel: HeroModel): Flow<Either<String, String>>

    fun getSkin(skinId: String): Flow<Either<String, SkinModel>>
    fun upsertSkin(skinModel: SkinModel): Flow<Either<String, String>>
    fun deleteSkin(skinModel: SkinModel): Flow<Either<String, String>>

    fun getReplace(replaceId: String): Flow<Either<String, ReplaceModel>>
    fun upsertReplace(replaceModel: ReplaceModel, platformFile: PlatformFile?): Flow<Either<String, String>>
    fun deleteReplace(replaceModel: ReplaceModel): Flow<Either<String, String>>

    fun cleanStorage(): Flow<Either<String, String>>
}