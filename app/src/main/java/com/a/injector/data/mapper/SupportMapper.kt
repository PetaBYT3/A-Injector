package com.a.injector.data.mapper

import com.a.injector.data.remote.support.SupportDto
import com.a.injector.domain.model.ProfileModel
import com.a.injector.domain.model.SupportModel

object SupportMapper: Mapper<SupportDto, SupportModel> {
    override fun toModel(dto: SupportDto): SupportModel {
        return SupportModel(
            id = dto.id,
            support = dto.support,
            imageUrl = dto.imageUrl,
            profile = if (dto.profile != null) {
                ProfileMapper.toModel(dto.profile)
            } else {
                ProfileModel.EMPTY
            }
        )
    }

    override fun toDto(model: SupportModel): SupportDto {
        return SupportDto(
            id = model.id,
            support = model.support,
            imageUrl = model.imageUrl,
            profile = null
        )
    }
}