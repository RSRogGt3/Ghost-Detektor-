package com.example.util

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.camera.core.ImageProxy
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.OutputStream
import java.nio.ByteBuffer

object ImageCaptureUtil {

    suspend fun saveTintedImageToGallery(
        context: Context,
        imageProxy: ImageProxy,
        primaryColor: Color
    ): Boolean = withContext(Dispatchers.IO) {
        try {
            val bitmap = imageProxyToBitmap(imageProxy)
            val tintedBitmap = applyFilterToBitmap(bitmap, primaryColor)
            val success = saveBitmapToMediaStore(context, tintedBitmap)
            imageProxy.close()
            success
        } catch (e: Exception) {
            e.printStackTrace()
            imageProxy.close()
            false
        }
    }

    private fun imageProxyToBitmap(image: ImageProxy): Bitmap {
        val planeProxy = image.planes[0]
        val buffer: ByteBuffer = planeProxy.buffer
        val bytes = ByteArray(buffer.remaining())
        buffer.get(bytes)
        val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        val matrix = Matrix()
        matrix.postRotate(image.imageInfo.rotationDegrees.toFloat())
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }

    private fun applyFilterToBitmap(original: Bitmap, primaryColor: Color): Bitmap {
        val result = Bitmap.createBitmap(original.width, original.height, original.config ?: Bitmap.Config.ARGB_8888)
        val canvas = Canvas(result)
        
        // Draw original
        canvas.drawBitmap(original, 0f, 0f, null)
        
        // Draw overlay 1: Monochromatic green spectral color wash
        val paint1 = Paint().apply {
            color = android.graphics.Color.argb(
                (0.65f * 255).toInt(), 0, 34, 8 // 0xFF002208
            )
        }
        canvas.drawRect(0f, 0f, result.width.toFloat(), result.height.toFloat(), paint1)
        
        // Draw overlay 2: High-contrast primary color glow
        val paint2 = Paint().apply {
            color = primaryColor.copy(alpha = 0.22f).toArgb()
        }
        canvas.drawRect(0f, 0f, result.width.toFloat(), result.height.toFloat(), paint2)
        
        return result
    }

    private fun saveBitmapToMediaStore(context: Context, bitmap: Bitmap): Boolean {
        val filename = "GhostDetector_Snapshot_${System.currentTimeMillis()}.jpg"
        var fos: OutputStream? = null
        var imageUri: Uri? = null
        
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
                put(MediaStore.Video.Media.IS_PENDING, 1)
            }
        }
        
        val resolver = context.contentResolver
        imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
        
        imageUri?.let { uri ->
            try {
                fos = resolver.openOutputStream(uri)
                fos?.let { bitmap.compress(Bitmap.CompressFormat.JPEG, 90, it) }
                
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    contentValues.clear()
                    contentValues.put(MediaStore.Video.Media.IS_PENDING, 0)
                    resolver.update(uri, contentValues, null, null)
                }
                return true
            } catch (e: Exception) {
                e.printStackTrace()
                return false
            } finally {
                fos?.close()
            }
        }
        return false
    }
}
