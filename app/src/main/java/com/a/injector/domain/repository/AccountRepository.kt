package com.a.injector.domain.repository

import arrow.core.Either
import com.a.injector.data.dto.Role
import com.a.injector.data.util.TextResource
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

    fun signIn(email: String, password: String): Flow<Either<TextResource, Unit>>
    fun signUp(email: String, password: String): Flow<Either<TextResource, Unit>>
    fun signGuest(): Flow<Either<TextResource, Unit>>
    fun signOut(): Flow<Either<TextResource, Unit>>
    fun sendResetPassword(email: String): Flow<Either<TextResource, TextResource>>

    fun getProfileByHighestContribution(): Flow<Either<TextResource, List<ProfileModel>>>
    fun upsertProfile(profileModel: ProfileModel): Flow<Either<TextResource, TextResource>>

    fun getRequestStatus(): Flow<RequestState>
    fun getRequestDetails(): Flow<Either<TextResource, List<RequestDetailModel>>>
    fun getGrantedByRole(role: Role): Flow<Either<TextResource, List<ProfileModel>>>
    fun upsertRequest(requestModel: RequestModel): Flow<Either<TextResource, TextResource>>
    fun grantRequest(requestModel: RequestModel): Flow<Either<TextResource, TextResource>>
}