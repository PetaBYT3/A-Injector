package com.a.injector.domain.repository

import arrow.core.Either
import com.a.injector.domain.model.InjectModel
import com.a.injector.domain.model.ReplaceModel
import com.a.injector.domain.model.Text
import kotlinx.coroutines.flow.Flow

interface InjectRepository {
    val currentInjectMethod: Flow<InjectModel>
    fun execute(replaceModel: ReplaceModel): Flow<Either<Text, Text>>
}