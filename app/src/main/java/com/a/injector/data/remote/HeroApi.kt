package com.a.injector.data.remote

import com.a.injector.data.dto.HeroDto
import kotlinx.coroutines.flow.Flow

interface HeroApi {
    fun getList(): Flow<List<HeroDto>>
    fun getSingle(heroId: String): Flow<HeroDto?>
    suspend fun upsert(hero: HeroDto)
    suspend fun delete(hero: HeroDto)
}