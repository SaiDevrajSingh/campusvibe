package com.example.campusvibe.utils

import android.content.Context
import android.net.Uri
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.UUID

suspend fun uploadImage(context: Context, uri: Uri, bucketName: String): String? {
    return withContext(Dispatchers.IO) {
        try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val fileBytes = inputStream?.readBytes()
            inputStream?.close()

            fileBytes?.let {
                val fileName = "${UUID.randomUUID()}"
                val supabase = SupabaseClient.client
                supabase.storage.from(bucketName).upload(fileName, it)
                return@withContext supabase.storage.from(bucketName).publicUrl(fileName)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return@withContext null
    }
}

suspend fun uploadVideo(context: Context, uri: Uri, bucketName: String): String? {
    return withContext(Dispatchers.IO) {
        try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val fileBytes = inputStream?.readBytes()
            inputStream?.close()

            fileBytes?.let {
                val fileName = "${UUID.randomUUID()}"
                val supabase = SupabaseClient.client
                supabase.storage.from(bucketName).upload(fileName, it)
                return@withContext supabase.storage.from(bucketName).publicUrl(fileName)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return@withContext null
    }
}
