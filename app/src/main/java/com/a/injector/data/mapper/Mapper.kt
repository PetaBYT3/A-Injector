package com.a.injector.data.mapper

interface Mapper<Dto, Model> {
    fun toModel(dto: Dto): Model
    fun toDto(model: Model): Dto
}