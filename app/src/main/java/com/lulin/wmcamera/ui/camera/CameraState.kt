package com.lulin.wmcamera.ui.camera

import android.graphics.Bitmap


/**
 *  @author Lulin
 *  @date 2024/1/23
 *  @desc
 */
sealed class CameraState {

    object TakePhoto : CameraState()

    data class Preview(val bitmap: Bitmap) : CameraState()

}