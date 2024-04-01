package com.lulin.wmcamera.ui.setting

import kotlinx.serialization.SerialInfo
import kotlinx.serialization.Serializable

/**
 *  @author Lulin
 *  @date 2024/2/27
 *  @desc
 */
@Serializable
data class SettingState(
    val userName: String = "",
    val deptName: String = "",
    val enableLocation: Boolean = true,
    val waterPosition: String = WaterPosition.BottomStart.position
)

@Serializable
enum class WaterPosition(val position: String) {
    TopStart("左上角"),
    TopEnd("右上角"),
    BottomStart("左下角"),
    BottomEnd("右下角")
}

val positionList = listOf(
    WaterPosition.TopStart.position,
    WaterPosition.TopEnd.position,
    WaterPosition.BottomStart.position,
    WaterPosition.BottomEnd.position,
)