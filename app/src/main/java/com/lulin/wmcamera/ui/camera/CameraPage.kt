package com.lulin.wmcamera.ui.camera

import android.Manifest
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIos
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

/**
 *  @author Lulin
 *  @date 2024/1/23
 *  @desc
 */
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CameraPage(
    viewModel: CameraViewModel = koinViewModel(),
    navSetting: () -> Unit
) {

    val cameraState = viewModel.uiState.collectAsState(initial = CameraState.TakePhoto)

    val settingState = viewModel.settingState.collectAsState()

    val scope = rememberCoroutineScope()

    val permissionState = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
    )

    LaunchedEffect(Unit) {
        permissionState.launchMultiplePermissionRequest()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
    ) {
        CameraView(
            state = cameraState.value,
            settingState = settingState.value,
            takePhoto = { imageCapture, waterMark ->
                scope.launch {
                    viewModel.mainIntentChannel.send(CameraIntent.TakePhoto(imageCapture, waterMark))
                }
            },
            onSave = {
                scope.launch {
                    viewModel.mainIntentChannel.send(CameraIntent.Save(it))
                }
            },
            onCancel = {
                scope.launch {
                    viewModel.mainIntentChannel.send(CameraIntent.Cancel)
                }
            },
            navSetting = { navSetting() }
        )
    }


}
