package com.a.injector.data.mapper

import com.a.injector.data.dto.SupportingDto
import com.a.injector.domain.model.ProfileModel
import com.a.injector.domain.model.SupportingModel

object SupportingMapper: Mapper<SupportingDto, SupportingModel> {
    override fun toModel(dto: SupportingDto): SupportingModel {
        return SupportingModel(
            id = dto.id,
            nominal = dto.nominal,
            imageUrl = dto.imageUrl,
            profile = if (dto.profile != null) {
                ProfileMapper.toModel(dto.profile)
            } else {
                ProfileModel.EMPTY
            }
        )
    }

    override fun toDto(model: SupportingModel): SupportingDto {
        return SupportingDto(
            id = model.id,
            nominal = model.nominal,
            imageUrl = model.imageUrl,
            profile = null
        )
    }
}