package com.a.injector.domain.repository

import arrow.core.Either
import com.a.injector.data.local.CommandService
import com.a.injector.domain.model.CommandServiceModel
import com.a.injector.domain.model.ReplaceModel
import kotlinx.coroutines.flow.Flow

interface InjectRepository {
    val commandService: Flow<CommandServiceModel>
    fun setCommandService(commandService: CommandService): Flow<Either<String, String>>

    fun start(replaceModel: ReplaceModel): Flow<Either<String, String>>
}