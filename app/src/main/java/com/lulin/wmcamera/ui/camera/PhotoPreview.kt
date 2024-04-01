package com.lulin.wmcamera.ui.camera

import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

/**
 *  @author Lulin
 *  @date 2024/1/23
 *  @desc
 */
@Composable
fun PhotoPreview(
    bitmap: Bitmap,
    onSave: (Bitmap) -> Unit,
    onCancel: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.Black)

    ) {
        AsyncImage(model = bitmap, contentDescription = null, modifier = Modifier.fillMaxSize())
        Box(
            modifier = Modifier
                .padding(bottom = 20.dp)
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
        ) {
           Row(
               horizontalArrangement = Arrangement.SpaceAround,
               modifier = Modifier.fillMaxWidth()
           ) {
               Text(
                   text = "取消",
                   color = Color.White,
                   fontSize = 18.sp,
                   modifier = Modifier
                       .clickable {
                           onCancel()
                           bitmap.recycle()
                       }
               )
               Text(
                   text = "保存",
                   color = Color.White,
                   fontSize = 18.sp,
                   modifier = Modifier
                       .clickable {
                           onSave(bitmap)
                       }
               )
           }
        }
    }


}