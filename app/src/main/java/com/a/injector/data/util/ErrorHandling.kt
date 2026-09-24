package com.a.injector.data.util

import com.a.injector.R
import io.github.jan.supabase.exceptions.RestException
import io.github.jan.supabase.postgrest.exception.PostgrestRestException
import io.ktor.client.network.sockets.ConnectTimeoutException
import java.net.ConnectException
import java.net.UnknownHostException

fun Throwable.toMessage(): TextResource {
    return when (this) {
        is PostgrestRestException -> {
            when (this.code) {
                "23505" -> {
                    TextResource.StringResource(R.string.exception_data_already_exist)
                }
                "23503" -> {
                    TextResource.StringResource(R.string.exception_no_data)
                }
                "PGRST116" -> {
                    TextResource.StringResource(R.string.exception_no_data)
                }
                else -> {
                    if (this.message != null) {
                        TextResource.DynamicString(this.message!!)
                    } else {
                        TextResource.StringResource(R.string.exception_server_error)
                    }
                }
            }
        }
        is RestException -> {
            val errorCode = this.error.lowercase()
            val description = this.description?.lowercase() ?: ""
            when {
                errorCode == "invalid_grant" || "invalid login credentials" in description -> {
                    TextResource.StringResource(R.string.exception_credential_invalid)
                }
                errorCode == "user_already_exists" || "already registered" in description -> {
                    TextResource.StringResource(R.string.exception_email_used)
                }
                errorCode == "over_email_send_rate_limit" || errorCode == "too_many_requests" || "rate limit" in description -> {
                    TextResource.StringResource(R.string.exception_too_many_attempts)
                }
                errorCode == "weak_password" || "password should be at least" in description -> {
                    TextResource.StringResource(R.string.exception_password_weak)
                }
                errorCode == "validation_failed" || "invalid email" in description -> {
                    TextResource.StringResource(R.string.exception_email_invalid)
                }
                errorCode == "email_not_confirmed" || "email not confirmed" in description -> {
                    TextResource.StringResource(R.string.exception_email_unverified)
                }
                errorCode == "user_not_found" -> {
                    TextResource.StringResource(R.string.exception_user_not_found)
                }
                else -> {
                    if (this.message != null) {
                        TextResource.DynamicString(this.message!!)
                    } else {
                        TextResource.StringResource(R.string.exception_server_error)
                    }
                }
            }
        }
        is ConnectTimeoutException -> {
            TextResource.StringResource(R.string.exception_connection_timeout)
        }
        is ConnectException, is UnknownHostException -> {
            TextResource.StringResource(R.string.exception_no_connection)
        }
        else -> {
            TextResource.StringResource(R.string.exception_unknown_error)
        }
    }
}