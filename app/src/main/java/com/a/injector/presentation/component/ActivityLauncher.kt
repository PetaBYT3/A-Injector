package com.a.injector.presentation.component

import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.core.net.toUri
import com.a.injector.BuildConfig

fun openStoragePermissionSettings(context: Context) {
    val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION).apply {
        data = "package:${BuildConfig.APPLICATION_ID}".toUri()
    }
    context.startActivity(intent)
}