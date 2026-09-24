package com.a.injector.presentation.util

import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.widget.Toast
import androidx.compose.ui.platform.UriHandler
import androidx.core.net.toUri
import com.a.injector.BuildConfig
import com.a.injector.R

fun openStoragePermissionSettings(context: Context) {
    val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION).apply {
        data = "package:${BuildConfig.APPLICATION_ID}".toUri()
        flags = Intent.FLAG_ACTIVITY_NEW_TASK
    }
    context.startActivity(intent)
}

fun openInBrowser(context: Context, uriHandler: UriHandler, url: String) {
    try {
        uriHandler.openUri(url)
    } catch (e: Exception) {
        Toast.makeText(context, context.getString(R.string.message_no_browser_installed), Toast.LENGTH_SHORT).show()
    }
}