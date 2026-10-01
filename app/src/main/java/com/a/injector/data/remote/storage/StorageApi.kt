package com.a.injector.data.remote.storage

import com.a.injector.domain.model.state.Bucket
import java.io.File

interface StorageApi {
    suspend fun getFileNames(fromBucket: Bucket): List<String>
    suspend fun getFileUrl(fromBucket: Bucket, fileName: String): String
    suspend fun upload(targetBucket: Bucket, fileByte: ByteArray, fileName: String): String
    suspend fun download(fromBucket: Bucket, fileName: String, outputPath: File)
    suspend fun delete(fromBucket: Bucket, files: List<String>)
}