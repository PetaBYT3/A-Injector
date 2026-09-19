package com.a.injector.data.mapper

import com.a.injector.data.dto.VersionDto
import com.a.injector.domain.model.VersionModel

object VersionMapper: Mapper<VersionDto, VersionModel> {
    override fun toModel(dto: VersionDto): VersionModel {
        return VersionModel(
            id = dto.id,
            platform = dto.platform,
            version = dto.version,
            maintenance = dto.maintenance
        )
    }

    override fun toDto(model: VersionModel): VersionDto {
        return VersionDto(
            id = model.id,
            platform = model.platform,
            version = model.version,
            maintenance = model.maintenance
        )
    }
}