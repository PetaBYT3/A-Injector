package com.a.injector.data.util

import android.content.ContentValues
import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore
import androidx.annotation.DrawableRes

fun drawableToUri(
    context: Context,
    fileName: String,
    @DrawableRes drawable: Int,
): Uri? {
    val bitmap = BitmapFactory.decodeResource(context.resources, drawable)
    val imageCollection = MediaStore.Images.Media.EXTERNAL_CONTENT_URI

    val contentValues = ContentValues().apply {
        put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
        put(MediaStore.Images.Media.MIME_TYPE, "image/png")
    }

    return context.contentResolver.insert(imageCollection, contentValues)
}