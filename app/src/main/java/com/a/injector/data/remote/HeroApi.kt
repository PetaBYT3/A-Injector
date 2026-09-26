package com.a.injector.data.remote

import com.a.injector.data.dto.HeroDto
import kotlinx.coroutines.flow.Flow

interface HeroApi {
    fun getHeroes(): Flow<List<HeroDto>>
    fun getHero(heroId: String): Flow<HeroDto?>
    suspend fun upsertHero(hero: HeroDto)
    suspend fun deleteHero(hero: HeroDto)
}