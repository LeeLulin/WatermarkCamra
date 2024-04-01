package com.lulin.wmcamera.ui.watermark

import androidx.compose.material.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Locale

/**
 *  @author Lulin
 *  @date 2024/1/25
 *  @desc
 */
@Composable
fun ClockView() {
    var currentTime by remember {
        mutableLongStateOf(0)
    }

    LaunchedEffect(Unit) {
        while (true) {
            currentTime = System.currentTimeMillis()
            delay(1000)
        }
    }

    val formattedTime = remember(currentTime) {
        SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(currentTime)
    }

    Text(
        text = formattedTime,
        color = Color.White,
        fontSize = 15.sp,
        style = LocalTextStyle.current.merge(
            TextStyle(shadow = Shadow(offset = Offset(1f, 1f), blurRadius = 1f))
        )
    )

}