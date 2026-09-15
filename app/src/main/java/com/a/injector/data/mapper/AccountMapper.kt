package com.a.injector.data.mapper

import com.a.injector.data.dto.ProfileDto
import com.a.injector.data.dto.RequestDetailDto
import com.a.injector.data.dto.RequestDto
import com.a.injector.domain.model.ProfileModel
import com.a.injector.domain.model.RequestDetailModel
import com.a.injector.domain.model.RequestModel

fun ProfileDto.toProfileModel(): ProfileModel = ProfileModel(
    id = this.id,
    email = this.email,
    role = this.role,
    contribution = this.contribution
)

fun ProfileModel.toProfileDto(): ProfileDto = ProfileDto(
    id = this.id,
    email = this.email,
    role = this.role,
    contribution = this.contribution
)

fun RequestDto.toRequestModel(): RequestModel = RequestModel(
    id = this.id,
    profileId = this.profileId ?: "",
    role = this.role
)

fun RequestModel.toRequestDto(): RequestDto = RequestDto(
    id = this.id,
    profileId = this.profileId.ifBlank { null },
    role = this.role
)

fun RequestDetailDto.toRequestDetailModel(): RequestDetailModel = RequestDetailModel(
    id = this.id,
    profileId = this.profileId ?: "",
    role = this.role,
    profile = this.profileDto?.toProfileModel() ?: ProfileModel.EMPTY
)