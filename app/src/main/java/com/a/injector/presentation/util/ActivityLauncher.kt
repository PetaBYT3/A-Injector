package com.a.injector.presentation.util

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.compose.ui.platform.UriHandler
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import com.a.injector.BuildConfig
import com.a.injector.R
import com.a.injector.domain.model.Text

fun openStoragePermissionSettings(context: Context) {
    val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION).apply {
        data = "package:${BuildConfig.APPLICATION_ID}".toUri()
        flags = Intent.FLAG_ACTIVITY_NEW_TASK
    }
    context.startActivity(intent)
}

fun openInBrowser(
    uriHandler: UriHandler,
    url: String,
    onError: (Text) -> Unit
) {
    try {
        uriHandler.openUri(url)
    } catch (e: Exception) {
        onError(Text.Resource(R.string.exception_no_handler))
    }
}

fun sendToEmail(
    context: Context,
    email: String,
    onError: (Text) -> Unit
) {
    val cleanEmail = email.trim()
    val mailUri = "mailto:$cleanEmail".toUri()

    val intent = Intent(Intent.ACTION_SENDTO, mailUri).apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }

    try {
        context.startActivity(intent)
    } catch (e: Exception) {
        onError(Text.Resource(R.string.exception_no_handler))
    }
}

fun copyToClipboard(
    context: Context,
    text: String
) {
    val clipboard = ContextCompat.getSystemService(context, ClipboardManager::class.java)
    if (clipboard != null) {
        val clip = ClipData.newPlainText("A Injector", text)
        clipboard.setPrimaryClip(clip)
    }
}