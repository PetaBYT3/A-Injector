package com.a.injector.data.mapper

import com.a.injector.data.dto.HeroDto
import com.a.injector.domain.model.HeroModel

object HeroMapper: Mapper<HeroDto, HeroModel> {
    override fun toModel(dto: HeroDto): HeroModel {
        return HeroModel(
            id = dto.id,
            name = dto.name,
            skins = dto.skins.map { SkinMapper.toModel(it) }
        )
    }

    override fun toDto(model: HeroModel): HeroDto {
        return HeroDto(
            id = model.id,
            name = model.name,
            skins = model.skins.map { SkinMapper.toDto(it) }
        )
    }
}