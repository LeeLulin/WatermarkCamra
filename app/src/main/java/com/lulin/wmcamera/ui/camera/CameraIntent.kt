package com.lulin.wmcamera.ui.camera

import android.graphics.Bitmap
import android.net.Uri
import androidx.camera.core.ImageCapture

/**
 *  @author Lulin
 *  @date 2024/1/23
 *  @desc
 */
sealed class CameraIntent {

    data class TakePhoto(val imageCapture: ImageCapture, val waterMark: Bitmap) : CameraIntent()

    data class Save(val bitmap: Bitmap) : CameraIntent()

    object Cancel : CameraIntent()
}