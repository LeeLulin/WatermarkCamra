package com.lulin.wmcamera.ui.watermark

import android.graphics.Bitmap
import android.graphics.Canvas
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.FrameLayout
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView

/**
 *  @author Lulin
 *  @date 2024/1/24
 *  @desc
 */

fun MutableState<WaterMarkState>.capture() {
    this.value = this.value.copy(capture = true)
}

fun MutableState<WaterMarkState>.captureComplete() {
    this.value = this.value.copy(capture = false)
}

@Composable
fun rememberCaptureController(): MutableState<WaterMarkState> {
    return remember {
        mutableStateOf(WaterMarkState(capture = false))
    }
}

@Composable
fun WaterMarkView(
    modifier: Modifier,
    captureController: MutableState<WaterMarkState> = rememberCaptureController(),
    onSaveBitmap: (Bitmap?) -> Unit,
    content: @Composable () -> Unit
) {
    val bounds = remember {
        mutableStateOf<Rect?>(null)
    }

    if (captureController.value.capture) {
        WaterMarkRender(
            modifier = modifier,
            captureController = captureController,
            bounds = bounds,
            onSaveBitmap = onSaveBitmap,
            content = content
        )
    } else {
        Surface(
            color = Color.Transparent,
            modifier = modifier
                .onGloballyPositioned {
                    bounds.value = it.boundsInRoot()
                },
            content = content
        )
    }
}

@Composable
fun WaterMarkRender(
    modifier: Modifier,
    captureController: MutableState<WaterMarkState>,
    bounds: MutableState<Rect?>,
    onSaveBitmap: ((Bitmap?) -> Unit)? = null,
    content: @Composable () -> Unit
) {
    AndroidView(
        modifier = modifier,
        factory = {
            FrameLayout(it).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ((bounds.value?.right ?: 0F) - (bounds.value?.left ?: 0F)).toInt(),
                    ((bounds.value?.bottom ?: 0F) -( bounds.value?.top ?: 0F)).toInt()
                )
                val composeView = ComposeView(it).apply {
                    setContent {
                        content()
                    }
                }
                drawListener(
                    composeView = composeView,
                    viewGroup = this,
                    captureController = captureController,
                    onSaveBitmap = onSaveBitmap
                )
                addView(
                    composeView,
                    ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                )
            }
        }
    )
}

private fun drawListener(
    composeView: View,
    viewGroup: ViewGroup,
    captureController: MutableState<WaterMarkState>,
    onSaveBitmap: ((Bitmap?) -> Unit)? = null,
) {
    val drawListener = object : ViewTreeObserver.OnDrawListener {
        var remove = false
        override fun onDraw() {
            if (composeView.width > 0) {
                if (!remove) {
                    // View 绘制第一帧 开始截图并移除 监听，随后切换截图状态 回到Compose组件
                    remove = true
                    composeView.post {
                        val bitmap = getViewGroupBitmap(viewGroup)
                        // 切换状态 回到Compose
                        captureController.captureComplete()
                        onSaveBitmap?.invoke(bitmap)
                        composeView.viewTreeObserver.removeOnDrawListener(this)
                    }
                }

            }
        }
    }
    composeView.viewTreeObserver.addOnDrawListener(drawListener)
}

/**
 * @param viewGroup viewGroup
 * @return Bitmap
 */
private fun getViewGroupBitmap(viewGroup: ViewGroup): Bitmap {
    val bitmap = Bitmap.createBitmap(viewGroup.width, viewGroup.height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    viewGroup.draw(canvas)
    return bitmap
}