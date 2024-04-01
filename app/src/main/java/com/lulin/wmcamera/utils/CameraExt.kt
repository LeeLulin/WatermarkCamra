package com.lulin.wmcamera.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import androidx.camera.core.ImageProxy
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 *  @author Lulin
 *  @date 2024/1/23
 *  @desc
 */
suspend fun Context.getCameraProvider(): ProcessCameraProvider =
    suspendCoroutine { continuation ->
        ProcessCameraProvider.getInstance(this).also { cameraProvider ->
            cameraProvider.addListener(
                {
                    continuation.resume(cameraProvider.get())
                }, ContextCompat.getMainExecutor(this)
            )
        }
    }

fun ImageProxy.createBitmap(): Bitmap {

    val bitmap = toBitmap()
    val rotation = imageInfo.rotationDegrees
    val matrix = Matrix()
    matrix.postRotate(rotation.toFloat())
    return Bitmap.createBitmap(
        bitmap,
        0,
        0,
        bitmap.width,
        bitmap.height,
        matrix,
        true
    )


}