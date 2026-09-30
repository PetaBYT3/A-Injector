package com.a.injector.data.mapper

import com.a.injector.data.dto.ProfileDto
import com.a.injector.domain.model.ProfileModel

object ProfileMapper: Mapper<ProfileDto, ProfileModel> {
    override fun toModel(dto: ProfileDto): ProfileModel {
        return ProfileModel(
            id = dto.id,
            username = dto.username,
            role = dto.role,
            contribution = dto.contribution,
            nominal = dto.support
        )
    }

    override fun toDto(model: ProfileModel): ProfileDto {
        return ProfileDto(
            id = model.id,
            username = model.username,
            role = model.role,
            contribution = model.contribution,
            support = model.nominal,
        )
    }
}