package com.a.injector.data.mapper

import com.a.injector.data.dto.SupporterDto
import com.a.injector.domain.model.SupporterModel

object SupporterMapper: Mapper<SupporterDto, SupporterModel> {
    override fun toModel(dto: SupporterDto): SupporterModel {
        return SupporterModel(
            id = dto.id,
            nominal = dto.nominal,
            profile = ProfileMapper.toModel(dto.profile)
        )
    }

    override fun toDto(model: SupporterModel): SupporterDto {
        return SupporterDto(
            id = model.id,
            nominal = model.nominal,
            profile = ProfileMapper.toDto(model.profile)
        )
    }
}