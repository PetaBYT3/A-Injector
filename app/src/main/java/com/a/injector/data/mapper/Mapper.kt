package com.a.injector.data.mapper

interface Mapper<D, M> {
    fun toModel(dto: D): M
    fun toDto(model: M): D
}