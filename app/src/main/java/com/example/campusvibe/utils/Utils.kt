package com.example.campusvibe.utils

import android.content.Context
import android.net.Uri
import android.util.Log
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.util.UUID
import kotlin.math.roundToInt

// Extension function to get file extension from URI
private fun getFileExtension(context: Context, uri: Uri): String {
    return context.contentResolver.getType(uri)?.split("/")?.lastOrNull() ?: ""
}

// Function to get file size from URI
private fun getFileSize(context: Context, uri: Uri): Long {
    return context.contentResolver.openFileDescriptor(uri, "r")?.use { pfd ->
        pfd.statSize
    } ?: 0L
}

/**
 * Uploads an image to Supabase Storage
 * @param context Application context
 * @param uri URI of the image to upload
 * @param bucketName Name of the bucket to upload to
 * @return Public URL of the uploaded image, or null if upload failed
 */
suspend fun uploadImage(context: Context, uri: Uri, bucketName: String): String? {
    return withContext(Dispatchers.IO) {
        try {
            // Check if URI is valid
            if (uri == Uri.EMPTY) {
                Log.e("UploadUtils", "Empty URI provided")
                return@withContext null
            }
            
            val inputStream = context.contentResolver.openInputStream(uri) ?: run {
                Log.e("UploadUtils", "Could not open input stream for URI: $uri")
                return@withContext null
            }
            
            try {
                val fileBytes = inputStream.readBytes()
                if (fileBytes.isEmpty()) {
                    Log.e("UploadUtils", "Empty file")
                    return@withContext null
                }
                
                val fileExtension = getFileExtension(context, uri).takeIf { it.isNotBlank() } ?: "jpg"
                val fileName = "${UUID.randomUUID()}.$fileExtension"
                val supabase = SupabaseClient.client
                
                Log.d("UploadUtils", "Attempting to upload to bucket: $bucketName")
                
                // Ensure bucket exists
                try {
                    supabase.storage[bucketName].list()
                } catch (e: Exception) {
                    Log.d("UploadUtils", "Creating bucket: $bucketName")
                    try {
                        supabase.storage.createBucket(bucketName) { public = true }
                    } catch (createError: Exception) {
                        Log.e("UploadUtils", "Error creating bucket $bucketName", createError)
                        return@withContext null
                    }
                }
                
                // Upload the file
                supabase.storage[bucketName].upload(fileName, fileBytes, true)
                
                // Get public URL
                val publicUrl = supabase.storage[bucketName].publicUrl(fileName)
                Log.d("UploadUtils", "Successfully uploaded to: $publicUrl")
                return@withContext publicUrl
                
            } catch (e: Exception) {
                Log.e("UploadUtils", "Error uploading to $bucketName", e)
                return@withContext null
            } finally {
                inputStream.close()
            }
            
        } catch (e: Exception) {
            Log.e("UploadUtils", "Error in upload process", e)
            return@withContext null
        }
    }
}

/**
 * Uploads a video to Supabase Storage with progress tracking
 * @param context Application context
 * @param uri URI of the video to upload
 * @param bucketName Name of the bucket to upload to
 * @param onProgress Callback for upload progress (0-100)
 * @return Public URL of the uploaded video, or null if upload failed
 */
suspend fun uploadVideo(
    context: Context, 
    uri: Uri, 
    bucketName: String,
    onProgress: (Float) -> Unit = {}
): String? {
    var retryCount = 0
    val maxRetries = 3
    
    while (retryCount < maxRetries) {
        var inputStream: InputStream? = null
        
        try {
            return withContext(Dispatchers.IO) {
                try {
                    inputStream = context.contentResolver.openInputStream(uri) ?: run {
                        Log.e("UploadUtils", "Could not open input stream for URI: $uri")
                        return@withContext null
                    }
                    
                    val fileSize = getFileSize(context, uri)
                    if (fileSize > 50 * 1024 * 1024) { // 50MB limit
                        Log.e("UploadUtils", "File too large: ${fileSize / (1024 * 1024)}MB")
                        return@withContext null
                    }
                    
                    val fileExtension = getFileExtension(context, uri).takeIf { it.isNotBlank() } ?: "mp4"
                    val fileName = "${UUID.randomUUID()}.$fileExtension"
                    val supabase = SupabaseClient.client
                    
                    // Ensure bucket exists
                    try {
                        supabase.storage[bucketName].list()
                    } catch (e: Exception) {
                        Log.d("UploadUtils", "Creating bucket: $bucketName")
                        try {
                            supabase.storage.createBucket(bucketName) { public = true }
                        } catch (createError: Exception) {
                            Log.e("UploadUtils", "Error creating bucket $bucketName", createError)
                            return@withContext null
                        }
                    }
                    
                    // Create a temporary file to store the video
                    val tempFile = File.createTempFile("temp_video_", ".$fileExtension", context.cacheDir)
                    
                    try {
                        // Copy the input stream to the temporary file
                        inputStream?.use { input ->
                            FileOutputStream(tempFile).use { output ->
                                val buffer = ByteArray(1024 * 1024) // 1MB buffer
                                var bytesRead: Int
                                var totalRead = 0L
                                
                                while (input.read(buffer).also { bytesRead = it } != -1) {
                                    output.write(buffer, 0, bytesRead)
                                    totalRead += bytesRead
                                    val progress = ((totalRead.toFloat() / fileSize.toFloat()) * 100).roundToInt().toFloat()
                                    onProgress(progress)
                                }
                            }
                        }
                        
                        // Upload the file using the file path
                        val fileBytes = tempFile.readBytes()
                        supabase.storage[bucketName].upload(
                            path = fileName,
                            data = fileBytes,
                            upsert = true
                        )
                        
                        // Get public URL
                        val publicUrl = supabase.storage[bucketName].publicUrl(fileName)
                        Log.d("UploadUtils", "Successfully uploaded to: $publicUrl")
                        return@withContext publicUrl
                        
                    } catch (e: Exception) {
                        Log.e("UploadUtils", "Error uploading video to $bucketName", e)
                        throw e
                    } finally {
                        // Clean up temp file
                        tempFile.delete()
                    }
                    
                } catch (e: Exception) {
                    Log.e("UploadUtils", "Error in video upload process", e)
                    throw e
                } finally {
                    inputStream?.close()
                }
            }
        } catch (e: Exception) {
            retryCount++
            if (retryCount >= maxRetries) {
                Log.e("UploadUtils", "Failed after $maxRetries attempts: ${e.message}", e)
                return null
            }
            // Wait before retrying (exponential backoff)
            val delayMs = 1000L * (retryCount * 2)
            Log.d("UploadUtils", "Retry $retryCount in ${delayMs}ms")
            try {
                delay(delayMs)
            } catch (ie: InterruptedException) {
                Thread.currentThread().interrupt()
                return null
            }
        }
    }
    return null
}
