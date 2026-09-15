package com.a.injector.domain.repository

import arrow.core.Either
import com.a.injector.data.dto.Role
import com.a.injector.domain.model.ProfileModel
import com.a.injector.domain.model.RequestDetailModel
import com.a.injector.domain.model.RequestModel
import com.a.injector.domain.model.state.AuthState
import kotlinx.coroutines.flow.Flow

interface AccountRepository {
    val currentAuth: Flow<AuthState>
    fun signIn(email: String, password: String): Flow<Either<String, Unit>>
    fun signUp(email: String, password: String): Flow<Either<String, Unit>>
    fun signGuest(): Flow<Either<String, Unit>>
    fun signOut(): Flow<Either<String, Unit>>

    fun upsertProfile(profileModel: ProfileModel): Flow<Either<String, String>>

    fun getRequestDetails(): Flow<Either<String, List<RequestDetailModel>>>
    fun getGrantedByRole(role: Role): Flow<Either<String, List<ProfileModel>>>
    fun upsertRequest(requestModel: RequestModel): Flow<Either<String, String>>
    fun grantRequest(requestModel: RequestModel): Flow<Either<String, String>>
}