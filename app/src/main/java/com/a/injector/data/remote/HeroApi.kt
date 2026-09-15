package com.a.injector.data.remote

import com.a.injector.data.dto.HeroDto
import com.a.injector.data.dto.HeroDetailDto
import kotlinx.coroutines.flow.Flow

interface HeroApi {
    fun getHeroDetails(): Flow<List<HeroDetailDto>>
    fun getHeroDetail(id: String): Flow<HeroDetailDto?>

    fun getHeroes(): Flow<List<HeroDto>>
    fun getHero(id: String): Flow<HeroDto?>
    suspend fun upsertHero(hero: HeroDto)
    suspend fun deleteHero(hero: HeroDto)
}