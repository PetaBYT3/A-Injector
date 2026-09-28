package com.a.injector.domain.repository

import arrow.core.Either
import com.a.injector.data.util.TextResource
import com.a.injector.domain.model.ProfileModel
import com.a.injector.domain.model.RoleModel
import com.a.injector.domain.model.SupportingModel
import io.github.vinceglb.filekit.PlatformFile
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun getProfiles(): Flow<Either<TextResource, List<ProfileModel>>>
    fun getProfile(profileId: String): Flow<Either<TextResource, ProfileModel>>
    fun upsertProfile(profileModel: ProfileModel): Flow<Either<TextResource, TextResource>>

    fun getTopSupporter(): Flow<Either<TextResource, List<ProfileModel>>>
    fun getTopContributor(): Flow<Either<TextResource, List<ProfileModel>>>


    fun getSupportingList(): Flow<Either<TextResource, List<SupportingModel>>>
    fun getSupporting(profileId: String): Flow<Either<TextResource, SupportingModel>>
    fun upsertSupporting(
        supportingModel: SupportingModel,
        image: PlatformFile?
    ): Flow<Either<TextResource, TextResource>>
    fun confirmSupporting(supportingModel: SupportingModel): Flow<Either<TextResource, TextResource>>
    fun denySupporting(supportingModel: SupportingModel): Flow<Either<TextResource, TextResource>>

    fun getRequests(): Flow<Either<TextResource, List<RoleModel>>>
    fun getRequest(profileId: String): Flow<Either<TextResource, RoleModel>>
    fun upsertRequest(roleModel: RoleModel): Flow<Either<TextResource, TextResource>>
    fun applyRequest(roleModel: RoleModel): Flow<Either<TextResource, TextResource>>
    fun grantRequest(roleModel: RoleModel): Flow<Either<TextResource, TextResource>>
}