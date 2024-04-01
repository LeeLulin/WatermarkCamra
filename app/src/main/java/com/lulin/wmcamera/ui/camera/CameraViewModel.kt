package com.lulin.wmcamera.ui.camera

import android.content.ContentValues
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import android.provider.MediaStore
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageProxy
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lulin.wmcamera.WMApplication
import com.lulin.wmcamera.repository.SettingRepository
import com.lulin.wmcamera.ui.setting.SettingState
import com.lulin.wmcamera.ui.setting.WaterPosition
import com.lulin.wmcamera.utils.createBitmap
import com.lulin.wmcamera.utils.saveToAlbum
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

/**
 *  @author Lulin
 *  @date 2024/1/23
 *  @desc
 */
class CameraViewModel(
    settingRepository: SettingRepository
): ViewModel() {

    companion object {
        const val DATE_FORMAT = "yyyy-MM-dd-HH-mm-ss"
    }

    val mainIntentChannel = Channel<CameraIntent>(Channel.UNLIMITED)

    private val _uiState = MutableStateFlow<CameraState>(CameraState.TakePhoto)
    val uiState: SharedFlow<CameraState> = _uiState

    val settingState: StateFlow<SettingState> = settingRepository.getSettingParams()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = SettingState()
        )

    init {
        viewModelScope.launch {
            mainIntentChannel.consumeAsFlow().collect {
                when (it) {
                    is CameraIntent.TakePhoto -> takePhoto(it.imageCapture, it.waterMark)
                    is CameraIntent.Save -> savePhoto(it.bitmap)
                    is CameraIntent.Cancel -> cancelSave()
                }
            }
        }
    }

    @androidx.annotation.OptIn(androidx.camera.core.ExperimentalGetImage::class)
    private fun takePhoto(
        imageCapture: ImageCapture,
        waterMark: Bitmap
    ) {
//        val name = SimpleDateFormat(DATE_FORMAT, Locale.CHINA).format(System.currentTimeMillis())
//        val contentValues = ContentValues().apply {
//            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
//            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
//        }
//
//        val outputOptions = ImageCapture.OutputFileOptions.Builder(
//            WMApplication.context.contentResolver,
//            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
//            contentValues
//        ).build()

        imageCapture.takePicture(
            ContextCompat.getMainExecutor(WMApplication.context),
            object : ImageCapture.OnImageCapturedCallback() {
                override fun  onCaptureSuccess(image: ImageProxy) {
                    val position = when(settingState.value.waterPosition) {
                        WaterPosition.TopStart.position -> WaterPosition.TopStart
                        WaterPosition.TopEnd.position -> WaterPosition.TopEnd
                        WaterPosition.BottomStart.position -> WaterPosition.BottomStart
                        WaterPosition.BottomEnd.position -> WaterPosition.BottomEnd
                        else -> WaterPosition.BottomStart
                    }
                    previewPhoto(image.createBitmap().addWaterMark(waterMark, position))
                    image.close()
                }
            }
        )

//        imageCapture.takePicture(
//            outputOptions,
//            ContextCompat.getMainExecutor(WMApplication.context),
//            object : ImageCapture.OnImageSavedCallback {
//                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
//                    previewPhoto(outputFileResults.savedUri!!)
//                }
//
//                override fun onError(exception: ImageCaptureException) {
//
//                }
//            }
//        )
    }

    private fun previewPhoto(bitmap: Bitmap) {
        viewModelScope.launch {
            _uiState.emit(CameraState.Preview(bitmap))
        }
    }

    private fun cancelSave() {
        viewModelScope.launch {
            _uiState.emit(CameraState.TakePhoto)
        }
    }

    private fun savePhoto(bitmap: Bitmap) {
        viewModelScope.launch {
            val fileName = "WMCamera-${SimpleDateFormat(DATE_FORMAT, Locale.CHINA).format(System.currentTimeMillis())}.jpg"
            bitmap.saveToAlbum(WMApplication.context, fileName)
            _uiState.emit(CameraState.TakePhoto)
        }
    }

    private fun Bitmap.addWaterMark(waterMark: Bitmap, position: WaterPosition = WaterPosition.BottomStart) : Bitmap {
        // 创建一个新的Bitmap，大小与原始Bitmap相同
        val resultBitmap = Bitmap.createBitmap(this.width, this.height, this.config)

        // 创建Canvas对象，用于在新的Bitmap上绘制内容
        val canvas = Canvas(resultBitmap)

        // 在Canvas上绘制原始Bitmap
        canvas.drawBitmap(this, 0f, 0f, null)

        val scaleWaterMark = Bitmap.createScaledBitmap(waterMark, waterMark.width * 2, waterMark.height * 2, true)

        // 计算水印位置，放在左下角
//        val x = 20.dp.value
//        val y = this.height - 20.dp.value - scaleWaterMark.height

        val x = when(position) {
            WaterPosition.BottomStart, WaterPosition.TopStart -> {
                20.dp.value
            }
            WaterPosition.BottomEnd, WaterPosition.TopEnd -> {
                this.width - 20.dp.value - scaleWaterMark.width
            }
        }

        val y = when(position) {
            WaterPosition.BottomStart, WaterPosition.BottomEnd -> {
                this.height - 20.dp.value - scaleWaterMark.height
            }
            WaterPosition.TopStart, WaterPosition.TopEnd -> {
                20.dp.value
            }
        }

        // 在Canvas上绘制水印Bitmap
        canvas.drawBitmap(scaleWaterMark, x, y, null)
        waterMark.recycle()
        scaleWaterMark.recycle()

        return resultBitmap
    }
}