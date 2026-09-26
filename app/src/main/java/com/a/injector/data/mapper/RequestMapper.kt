package com.a.injector.data.mapper

import com.a.injector.data.dto.RequestDto
import com.a.injector.domain.model.ProfileModel
import com.a.injector.domain.model.RequestModel

object RequestMapper: Mapper<RequestDto, RequestModel> {
    override fun toModel(dto: RequestDto): RequestModel {
        return RequestModel(
            id = dto.id,
            role = dto.role,
            profile = if (dto.profile != null) {
                ProfileMapper.toModel(dto.profile)
            } else {
                ProfileModel.EMPTY
            }
        )
    }

    override fun toDto(model: RequestModel): RequestDto {
        return RequestDto(
            id = model.id,
            role = model.role,
            profile = null,
        )
    }
}