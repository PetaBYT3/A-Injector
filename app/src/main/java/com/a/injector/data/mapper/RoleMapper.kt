package com.a.injector.data.mapper

import com.a.injector.data.dto.RoleDto
import com.a.injector.domain.model.ProfileModel
import com.a.injector.domain.model.RoleModel

object RoleMapper: Mapper<RoleDto, RoleModel> {
    override fun toModel(dto: RoleDto): RoleModel {
        return RoleModel(
            id = dto.id,
            role = dto.role,
            profile = if (dto.profile != null) {
                ProfileMapper.toModel(dto.profile)
            } else {
                ProfileModel.EMPTY
            }
        )
    }

    override fun toDto(model: RoleModel): RoleDto {
        return RoleDto(
            id = model.id,
            role = model.role,
            profile = null,
        )
    }
}