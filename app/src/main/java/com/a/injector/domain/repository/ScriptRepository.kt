package com.a.injector.domain.repository

import arrow.core.Either
import com.a.injector.domain.model.HeroModel
import com.a.injector.domain.model.ReplaceModel
import com.a.injector.domain.model.SkinModel
import com.a.injector.domain.model.Text
import io.github.vinceglb.filekit.PlatformFile
import kotlinx.coroutines.flow.Flow

interface ScriptRepository {
    fun getHeroes(): Flow<Either<Text, List<HeroModel>>>
    fun getHero(heroId: String): Flow<Either<Text, HeroModel>>
    fun upsertHero(heroModel: HeroModel): Flow<Either<Text, Unit>>
    fun deleteHero(heroModel: HeroModel): Flow<Either<Text, Unit>>

    fun getSkin(skinId: String): Flow<Either<Text, SkinModel>>
    fun upsertSkin(skinModel: SkinModel): Flow<Either<Text, Unit>>
    fun deleteSkin(skinModel: SkinModel): Flow<Either<Text, Unit>>

    fun getReplace(replaceId: String): Flow<Either<Text, ReplaceModel>>
    fun upsertReplace(
        replaceModel: ReplaceModel,
        replaceFile: PlatformFile?
    ): Flow<Either<Text, Unit>>
    fun deleteReplace(replaceModel: ReplaceModel): Flow<Either<Text, Unit>>
}