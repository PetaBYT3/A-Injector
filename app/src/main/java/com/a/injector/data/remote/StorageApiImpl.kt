@file:Suppress("BlockingMethodInNonBlockingContext")

package com.a.injector.data.remote

import com.a.injector.data.dto.Bucket
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.storage.storage
import org.koin.core.annotation.Single
import java.io.File
import java.io.FileOutputStream

@Single
class StorageApiImpl(
    private val supabaseClient: SupabaseClient
): StorageApi {
    override suspend fun upload(
        targetBucket: Bucket,
        fileByte: ByteArray,
        fileName: String,
    ) {
        supabaseClient.storage.from(targetBucket.absoluteName).upload(
            path = fileName,
            data = fileByte,
            options = {
                upsert = true
            }
        )
    }

    override suspend fun download(
        fromBucket: Bucket,
        fileName: String,
        outputPath: File
    ) {
        val fileBytes = supabaseClient.storage.from(fromBucket.absoluteName).downloadPublic(fileName)
        FileOutputStream(outputPath).use { fileOutputStream ->
            fileOutputStream.write(fileBytes)
        }
    }

    override suspend fun delete(
        fromBucket: Bucket,
        fileName: String
    ) {
        supabaseClient.storage.from(fromBucket.absoluteName).delete(fileName)
    }
}