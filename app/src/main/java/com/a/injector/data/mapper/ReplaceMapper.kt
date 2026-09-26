package com.a.injector.data.mapper

import com.a.injector.data.dto.ReplaceDto
import com.a.injector.domain.model.ReplaceModel

object ReplaceMapper: Mapper<ReplaceDto, ReplaceModel> {
    override fun toModel(dto: ReplaceDto): ReplaceModel {
        return ReplaceModel(
            id = dto.id,
            skinId = dto.skinId,
            label = dto.label,
            name = dto.name,
            lastUpdate = dto.lastUpdate,
            fileSize = dto.fileSize
        )
    }

    override fun toDto(model: ReplaceModel): ReplaceDto {
        return ReplaceDto(
            id = model.id,
            skinId = model.skinId,
            label = model.label,
            name = model.name,
            lastUpdate = model.lastUpdate,
            fileSize = model.fileSize
        )
    }
}