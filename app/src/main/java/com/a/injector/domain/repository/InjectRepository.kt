package com.a.injector.domain.repository

import arrow.core.Either
import com.a.injector.data.util.TextResource
import com.a.injector.domain.model.CommandServiceModel
import com.a.injector.domain.model.ReplaceModel
import com.a.injector.domain.model.state.CommandService
import kotlinx.coroutines.flow.Flow

interface InjectRepository {
    val commandService: Flow<CommandServiceModel>
    fun setCommandService(commandService: CommandService): Flow<Either<TextResource, TextResource>>

    fun start(replaceModel: ReplaceModel): Flow<Either<TextResource, TextResource>>
    fun execute(replaceModel: ReplaceModel): Flow<Either<TextResource, TextResource>>
}