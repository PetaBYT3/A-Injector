package com.a.injector.data.mapper

import com.a.injector.data.dto.HeroDto
import com.a.injector.data.dto.HeroDetailDto
import com.a.injector.data.dto.ReplaceDto
import com.a.injector.data.dto.SkinDto
import com.a.injector.data.dto.SkinDetailDto
import com.a.injector.domain.model.HeroModel
import com.a.injector.domain.model.HeroDetailModel
import com.a.injector.domain.model.ReplaceModel
import com.a.injector.domain.model.SkinModel
import com.a.injector.domain.model.SkinDetailModel

fun HeroDto.toHeroModel(): HeroModel = HeroModel(
    id = this.id,
    name = this.name
)

fun HeroDetailDto.toHeroWithSkinModel(): HeroDetailModel = HeroDetailModel(
    id = this.id,
    name = this.name,
    skins = this.skins.map { it.toSkinWithReplaceModel() }
)

fun SkinDto.toSkinModel(): SkinModel = SkinModel(
    id = this.id,
    heroId = this.heroId,
    label = this.label,
    name = this.name
)

fun SkinDetailDto.toSkinWithReplaceModel(): SkinDetailModel = SkinDetailModel(
    id = this.id,
    heroId = this.heroId,
    label = this.label,
    name = this.name,
    replaces = this.replaces.map { it.toReplaceModel() }
)

fun ReplaceDto.toReplaceModel(): ReplaceModel = ReplaceModel(
    id = this.id,
    skinId = this.skinId,
    label = this.label,
    name = this.name,
    lastUpdate = this.lastUpdate,
    fileSize = this.fileSize
)

fun HeroModel.toHeroDto(): HeroDto = HeroDto(
    id = this.id,
    name = this.name
)

fun SkinModel.toSkinDto(): SkinDto = SkinDto(
    id = this.id,
    heroId = this.heroId,
    label = this.label,
    name = this.name
)

fun ReplaceModel.toReplaceDto(): ReplaceDto = ReplaceDto(
    id = this.id,
    skinId = this.skinId,
    label = this.label,
    name = this.name,
    lastUpdate = null,
    fileSize = null
)