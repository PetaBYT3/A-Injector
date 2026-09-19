package com.a.injector.domain.repository

import arrow.core.Either
import com.a.injector.data.dto.Role
import com.a.injector.domain.model.ProfileModel
import com.a.injector.domain.model.RequestDetailModel
import com.a.injector.domain.model.RequestModel
import com.a.injector.domain.model.state.AuthResult
import com.a.injector.domain.model.state.RequestState
import io.github.jan.supabase.auth.user.UserInfo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface AccountRepository {
    val authState: StateFlow<AuthResult?>
    val currentUserInfo: StateFlow<UserInfo?>
    val currentProfile: StateFlow<ProfileModel>

    fun signIn(email: String, password: String): Flow<Either<String, Unit>>
    fun signUp(email: String, password: String): Flow<Either<String, Unit>>
    fun signGuest(): Flow<Either<String, Unit>>
    fun signOut(): Flow<Either<String, Unit>>

    fun getProfileByHighestContribution(): Flow<Either<String, List<ProfileModel>>>
    fun upsertProfile(profileModel: ProfileModel): Flow<Either<String, String>>

    fun getRequestStatus(): Flow<RequestState>
    fun getRequestDetails(): Flow<Either<String, List<RequestDetailModel>>>
    fun getGrantedByRole(role: Role): Flow<Either<String, List<ProfileModel>>>
    fun upsertRequest(requestModel: RequestModel): Flow<Either<String, String>>
    fun grantRequest(requestModel: RequestModel): Flow<Either<String, String>>
}