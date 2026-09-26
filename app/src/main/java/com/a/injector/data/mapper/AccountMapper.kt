package com.a.injector.data.mapper

import com.a.injector.data.dto.ProfileDto
import com.a.injector.data.dto.RequestDto
import com.a.injector.domain.model.ProfileModel
import com.a.injector.domain.model.RequestDetailModel
import com.a.injector.domain.model.RequestModel

fun ProfileDto.toProfileModel(): ProfileModel = ProfileModel(
    id = this.id ?: "",
    username = username,
    role = this.role,
    contribution = this.contribution
)

fun ProfileModel.toProfileDto(): ProfileDto = ProfileDto(
    id = this.id,
    username = username,
    role = this.role,
    contribution = this.contribution
)

fun RequestDto.toRequestModel(): RequestModel = RequestModel(
    id = this.id,
    role = role
)

fun RequestModel.toRequestDto(): RequestDto = RequestDto(
    id = this.id,
    role = role
)

fun RequestDto.toRequestDetailModel(): RequestDetailModel = RequestDetailModel(
    id = this.id,
    role = role,
    profile = profileDto?.toProfileModel() ?: ProfileModel.EMPTY
)