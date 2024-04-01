package com.lulin.wmcamera.ui.widget

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSizeIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 *  @author Lulin
 *  @date 2024/1/31
 *  @desc
 */
@Composable
fun <T>SelectMenu(
    modifier: Modifier,
    defaultValue: String,
    keyName: String?,
    list: List<T>,
    onSelected: (T) -> Unit
) {
    val show = remember { mutableStateOf(false) }
    val bounds = remember { mutableStateOf<Rect?>(null) }
    val animateRotate by animateFloatAsState(
        targetValue = if (show.value) 180F else 0F,
        label = "rotate"
    )
    Box(
        modifier = Modifier
            .then(modifier)
            .fillMaxWidth(),
        contentAlignment = Alignment.CenterEnd
    ) {
        Row(
            modifier = Modifier
                .clickable {
                    show.value = true
                }
                .onGloballyPositioned {
                    bounds.value = it.boundsInRoot()
                },
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (defaultValue.isEmpty()) {
                Text(
                    text = "请选择",
                    fontSize = 14.sp,
                    color = Color.Gray,
                )
            } else {
                Text(
                    text = defaultValue,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Right,
                )
            }
            Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = null,
                modifier = Modifier
                    .size(18.dp)
                    .rotate(animateRotate)
            )
        }

        Box(
            modifier = Modifier.padding(top = bounds.value?.height?.div(2)?.dp ?: 0.dp),
            contentAlignment = Alignment.BottomEnd
        ) {
            DropdownMenu(
                expanded = show.value,
                onDismissRequest = { show.value = false },
                modifier = Modifier
                    .width(IntrinsicSize.Min)
                    .requiredSizeIn(maxHeight = 300.dp)
                    .wrapContentSize(Alignment.TopEnd)
                    .background(color = Color.White)
            ) {
                list.forEach { item ->
                    DropdownMenuItem(
                        text = {
                            Text(text = if (item is String) item else item.getValueByKey(key = keyName ?: ""))
                        },
                        onClick = {
                            onSelected(item)
                            show.value = false
                        }
                    )
                }
            }
        }
    }
}

fun <T> T.getValueByKey(key: String): String {
    return this!!::class.members.find { it.name == key }?.call(this).toString()
}
