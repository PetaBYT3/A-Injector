package com.a.injector.data.mapper

import com.a.injector.data.dto.SkinDto
import com.a.injector.domain.model.SkinModel

object SkinMapper : Mapper<SkinDto, SkinModel> {
    override fun toModel(dto: SkinDto): SkinModel {
        return SkinModel(
            id = dto.id,
            heroId = dto.heroId,
            label = dto.label,
            name = dto.name,
            replaces = dto.replaces.map { ReplaceMapper.toModel(it) }
        )
    }

    override fun toDto(model: SkinModel): SkinDto {
        return SkinDto(
            id = model.id,
            heroId = model.heroId,
            name = model.name,
            label = model.label,
            replaces = model.replaces.map { ReplaceMapper.toDto(it) }
        )
    }
}