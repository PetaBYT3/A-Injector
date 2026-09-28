package com.a.injector.data.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.readBytes
import java.io.ByteArrayOutputStream

suspend fun PlatformFile.toWebpByteArray(quality: Int = 75): ByteArray {
    val rawBytes = this.readBytes()

    val bitmap = BitmapFactory.decodeByteArray(rawBytes, 0, rawBytes.size)
    val outputStream = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.WEBP_LOSSY, quality, outputStream)
    bitmap.recycle()

    return outputStream.toByteArray()
}