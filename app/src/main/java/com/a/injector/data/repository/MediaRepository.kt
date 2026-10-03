package com.a.injector.data.repository

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.provider.MediaStore
import arrow.core.Either
import com.a.injector.R
import com.a.injector.data.util.catchAndDispatch
import com.a.injector.domain.model.Text
import com.a.injector.domain.repository.MediaRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.koin.core.annotation.Single

@Single
class MediaRepository(
    private val context: Context
): MediaRepository {
    override fun downloadDrawable(
        drawable: Int,
        fileName: String
    ): Flow<Either<Text, Text>> {
        return flow<Either<Text, Text>> {
            val bitmap = BitmapFactory.decodeResource(context.resources, drawable)
            val imageCollection = MediaStore.Images.Media.EXTERNAL_CONTENT_URI

            val contextValues = ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, "$fileName.png")
                put(MediaStore.Images.Media.MIME_TYPE, "image/png")
            }

            val imageUri = context.contentResolver.insert(imageCollection, contextValues)
            if (imageUri == null) {
                emit(Either.Left(Text.Resource(R.string.exception_unknown)))
                return@flow
            }

            context.contentResolver.openOutputStream(imageUri)?.use { outputStream ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            }
            emit(Either.Right(Text.Resource(R.string.success_image_save)))
        }.catchAndDispatch()
    }
}