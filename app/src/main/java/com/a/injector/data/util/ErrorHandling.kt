package com.a.injector.data.util

import android.os.DeadObjectException
import android.os.RemoteException
import com.a.injector.R
import com.a.injector.data.remote.SupabaseConst
import com.a.injector.domain.model.Text
import io.github.jan.supabase.exceptions.RestException
import io.github.jan.supabase.postgrest.exception.PostgrestRestException
import io.ktor.client.network.sockets.ConnectTimeoutException
import java.net.ConnectException
import java.net.UnknownHostException

fun Throwable.toMessage(): Text {
    return when (this) {
        is PostgrestRestException -> {
            when (this.code) {
                "23505" -> {
                    Text.Resource(R.string.exception_data_already_exist)
                }
                "23503" -> {
                    Text.Resource(R.string.exception_no_data)
                }
                "PGRST116" -> {
                    Text.Resource(R.string.exception_no_data)
                }
                else -> {
                    if (SupabaseConst.IS_DEBUG_ENABLED) {
                        Text.Static(this.message ?: "Unknown Error")
                    } else {
                        Text.Resource(R.string.exception_server_error)
                    }
                }
            }
        }
        is RestException -> {
            val errorCode = this.error.lowercase()
            val description = this.description?.lowercase() ?: ""
            when {
                errorCode == "invalid_grant" || "invalid login credentials" in description -> {
                    Text.Resource(R.string.exception_credential_invalid)
                }
                errorCode == "user_already_exists" || "already registered" in description -> {
                    Text.Resource(R.string.exception_email_used)
                }
                errorCode == "over_email_send_rate_limit" || errorCode == "too_many_requests" || "rate limit" in description -> {
                    Text.Resource(R.string.exception_too_many_attempts)
                }
                errorCode == "weak_password" || "password should be at least" in description -> {
                    Text.Resource(R.string.exception_password_weak)
                }
                errorCode == "validation_failed" || "invalid email" in description -> {
                    Text.Resource(R.string.exception_email_invalid)
                }
                errorCode == "email_not_confirmed" || "email not confirmed" in description -> {
                    Text.Resource(R.string.exception_email_unverified)
                }
                errorCode == "user_not_found" -> {
                    Text.Resource(R.string.exception_user_not_found)
                }
                else -> {
                    if (SupabaseConst.IS_DEBUG_ENABLED) {
                        Text.Static(this.message ?: "Unknown Error")
                    } else {
                        Text.Resource(R.string.exception_server_error)
                    }
                }
            }
        }
        is ConnectTimeoutException -> {
            Text.Resource(R.string.exception_connection_timeout)
        }
        is ConnectException, is UnknownHostException -> {
            Text.Resource(R.string.exception_no_connection)
        }
        is ManageStoragePermissionDenied -> {
            Text.Resource(R.string.exception_manage_storage_permission_denied)
        }
        is ManageStorageMethodFailed -> {
            Text.Resource(R.string.exception_manage_storage_failed)
        }
        is ShizukuUnauthorized -> {
            Text.Resource(R.string.exception_shizuku_unauthorized)
        }
        is ShizukuMethodFailed -> {
            Text.Combined(
                listOf(
                    Text.Resource(R.string.shizuku_method_failed),
                    Text.Static(": $message")
                )
            )
        }
        is SuperuserDenied -> {
            Text.Resource(R.string.superuser_denied)
        }
        is SuperuserMethodFailed -> {
            Text.Combined(
                listOf(
                    Text.Resource(R.string.superuser_method_failed),
                    Text.Static(": $message")
                )
            )
        }
        is RemoteException, is DeadObjectException -> {
            Text.Combined(
                listOf(
                    Text.Resource(R.string.shizuku_method_failed),
                    Text.Static(": ${this.localizedMessage}")
                )
            )
        }
        is SecurityException -> {
            Text.Combined(
                listOf(
                    Text.Resource(R.string.exception_security),
                    Text.Static(": ${this.localizedMessage}")
                )
            )
        }
        else -> {
            val message = this.message
            if (message != null) {
                Text.Static(message)
            } else {
                Text.Resource(R.string.exception_unknown_error)
            }
        }
    }
}