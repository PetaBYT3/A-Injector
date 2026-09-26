package com.a.injector.data.remote

import com.a.injector.data.dto.Bucket
import java.io.File

interface StorageApi {
    suspend fun getFileNames(fromBucket: Bucket): List<String>
    suspend fun upload(targetBucket: Bucket, fileByte: ByteArray, fileName: String)
    suspend fun download(fromBucket: Bucket, fileName: String, outputPath: File)
    suspend fun delete(fromBucket: Bucket, files: List<String>)
}