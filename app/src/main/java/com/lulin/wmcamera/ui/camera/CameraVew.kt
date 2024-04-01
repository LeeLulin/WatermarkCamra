package com.lulin.wmcamera.ui.camera

import android.content.res.Resources
import android.graphics.Bitmap
import android.util.Log
import androidx.annotation.Px
import androidx.camera.core.AspectRatio
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.IconButton
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.constraintlayout.compose.ConstraintLayout
import com.lulin.wmcamera.R
import com.lulin.wmcamera.ui.setting.SettingState
import com.lulin.wmcamera.ui.setting.WaterPosition
import com.lulin.wmcamera.ui.watermark.ClockView
import com.lulin.wmcamera.ui.watermark.WaterMarkView
import com.lulin.wmcamera.ui.watermark.capture
import com.lulin.wmcamera.ui.watermark.rememberCaptureController
import com.lulin.wmcamera.utils.getCameraProvider

/**
 *  @author Lulin
 *  @date 2024/1/23
 *  @desc
 */

@Composable
fun CameraView(
    state: CameraState,
    settingState: SettingState,
    takePhoto: (ImageCapture, Bitmap) -> Unit,
    onSave: (Bitmap) -> Unit,
    onCancel: () -> Unit,
    navSetting: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val preview = Preview.Builder().apply {
        setTargetAspectRatio(AspectRatio.RATIO_16_9)
    }.build()

    val previewView = PreviewView(context)

    val cameraSelector = CameraSelector.Builder()
        .requireLensFacing(CameraSelector.LENS_FACING_BACK)
        .build()

    val imageCapture = remember {
        ImageCapture.Builder().apply {
            setTargetAspectRatio(AspectRatio.RATIO_16_9)
        }.build()
    }

    LaunchedEffect(CameraSelector.LENS_FACING_BACK) {
        val cameraProvider = context.getCameraProvider()
        cameraProvider.unbindAll()
        cameraProvider.bindToLifecycle(
            lifecycleOwner,
            cameraSelector,
            preview,
            imageCapture
        )
        preview.setSurfaceProvider(previewView.surfaceProvider)
    }

    val captureController = rememberCaptureController()

    var actionHeight by remember {
        mutableIntStateOf(0)
    }

    var settingHeight by remember {
        mutableIntStateOf(0)
    }

    Box(
        modifier = Modifier
            .systemBarsPadding()
            .background(color = Color.Black)
            .fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            AndroidView(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = pxToDp(actionHeight)),
                factory = { previewView }
            )
            Row(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .fillMaxWidth()
                    .background(color = Color.Black)
                    .padding(horizontal = 5.dp, vertical = 2.dp)
                    .onSizeChanged {
                        settingHeight = it.height
                    },
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { navSetting() }
                ) {
                    Icon(imageVector = Icons.Default.Settings, contentDescription = null, modifier = Modifier.size(24.dp), tint = Color.White)
                }
            }
            val align = when(settingState.waterPosition) {
                WaterPosition.TopStart.position -> Alignment.TopStart
                WaterPosition.TopEnd.position -> Alignment.TopEnd
                WaterPosition.BottomStart.position -> Alignment.BottomStart
                WaterPosition.BottomEnd.position -> Alignment.BottomEnd
                else -> Alignment.BottomStart
            }
            WaterMarkView(
                modifier = Modifier
                    .align(align)
                    .padding(
                        start = if (align == Alignment.TopStart || align == Alignment.BottomStart)
                            10.dp
                        else
                            0.dp,
                        end = if (align == Alignment.TopEnd || align == Alignment.BottomEnd)
                            10.dp
                        else
                            0.dp,
                        top = if (align == Alignment.TopEnd || align == Alignment.TopStart)
                            pxToDp(px = settingHeight) + 10.dp
                        else
                            0.dp,
                        bottom = if (align == Alignment.BottomStart || align == Alignment.BottomEnd)
                            pxToDp(actionHeight) + 10.dp
                        else
                            0.dp
                    ),
                captureController = captureController,
                onSaveBitmap = { bitmap ->
                    bitmap?.let {
                        takePhoto.invoke(imageCapture, it)
                    }
                }
            ) {

                Column(
                    verticalArrangement = Arrangement.spacedBy(5.dp),
                    modifier = Modifier.background(color = Color.Transparent)
                ) {
                    val textStyle = LocalTextStyle.current.merge(
                        TextStyle(shadow = Shadow(offset = Offset(1f, 1f), blurRadius = 1f))
                    )
                    Text(
                        text = "${settingState.deptName}-${settingState.userName}",
                        color = Color.White,
                        fontSize = 15.sp,
                        style = textStyle
                    )
                    ClockView()
                    if (settingState.enableLocation) {
                        Row(
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Image(
                                modifier = Modifier
                                    .size(20.dp)
                                    .padding(end = 4.dp),
                                painter = painterResource(id = R.drawable.ic_location),
                                contentDescription = null
                            )
                            Text(text = "波江座-致远星", color = Color.White, fontSize = 15.sp, style = textStyle)
                        }
                    }
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .align(Alignment.BottomStart)
                    .onSizeChanged {
                        Log.d("SizeChange", it.height.toString())
                        actionHeight = it.height
                    },
                horizontalArrangement = Arrangement.Center,
                verticalAlignment =Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_take_photo),
                    contentDescription = null,
                    modifier = Modifier
                        .size(70.dp)
                        .wrapContentSize()
                        .align(Alignment.CenterVertically)
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onTap = {
                                    captureController.capture()
                                }
                            )
                        }
                )
            }
        }
//        Column(
//            modifier = Modifier
//                .background(color = Color.Black)
//                .fillMaxSize(),
//            verticalArrangement = Arrangement.SpaceBetween
//        ) {
//
//
//        }

        if (state is CameraState.Preview) {
            PhotoPreview(
                bitmap = state.bitmap,
                onSave = { onSave(it) },
                onCancel = { onCancel() }
            )
        }
    }
}

@Composable
fun pxToDp(px: Int): Dp {
    return LocalDensity.current.run {
        px.toDp()
    }
}

@androidx.compose.ui.tooling.preview.Preview
@Composable
fun CameraViewPreview() {
    CameraView(
        state = CameraState.TakePhoto,
        settingState = SettingState(),
        takePhoto = { imageCapture, waterMark ->

        },
        onSave = {

        },
        onCancel = {

        },
        navSetting = {

        }
    )
}

