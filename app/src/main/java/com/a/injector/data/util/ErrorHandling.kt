package com.a.injector.data.util

import android.content.Context
import com.a.injector.R
import io.github.jan.supabase.exceptions.RestException
import io.github.jan.supabase.postgrest.exception.PostgrestRestException
import io.ktor.client.network.sockets.ConnectTimeoutException
import java.net.ConnectException
import java.net.UnknownHostException

fun Throwable.toMessage(context: Context): String {
    return when (this) {
        is PostgrestRestException -> {
            when (this.code) {
                "23505" -> {
                    context.getString(R.string.message_data_already_exist)
                }
                "23503" -> {
                    "Data gagal diproses karena referensi tidak ditemukan (Foreign Key Violation)"
                }
                "PGRST116" -> {
                    // Data tidak ditemukan (jika pakai decodeSingle)
                    context.getString(R.string.message_data_already_exist)
                }
                else -> {
                    this.message ?: context.getString(R.string.message_server_error)
                }
            }
        }
        is RestException -> {
            val errorCode = this.error.lowercase()
            val description = this.description?.lowercase() ?: ""
            when {
                errorCode == "invalid_grant" || "invalid login credentials" in description -> {
                    context.getString(R.string.message_credential_invalid)
                }
                errorCode == "user_already_exists" || "already registered" in description -> {
                    context.getString(R.string.message_email_used)
                }
                errorCode == "over_email_send_rate_limit" || errorCode == "too_many_requests" || "rate limit" in description -> {
                    context.getString(R.string.message_too_many_attempts)
                }
                errorCode == "weak_password" || "password should be at least" in description -> {
                    context.getString(R.string.message_password_weak)
                }
                errorCode == "validation_failed" || "invalid email" in description -> {
                    context.getString(R.string.message_email_invalid)
                }
                errorCode == "email_not_confirmed" || "email not confirmed" in description -> {
                    context.getString(R.string.message_email_unverified)
                }
                errorCode == "user_not_found" -> {
                    context.getString(R.string.message_user_not_found)
                }
                else -> {
                    this.description ?: context.getString(R.string.message_server_error)
                }
            }
        }
        is ConnectTimeoutException -> {
            context.getString(R.string.message_connection_timeout)
        }
        is ConnectException, is UnknownHostException -> {
            context.getString(R.string.message_no_connection)
        }
        else -> {
            this.message ?: context.getString(R.string.message_unknown_error)
        }
    }
}