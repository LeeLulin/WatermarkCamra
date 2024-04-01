package com.lulin.wmcamera.ui.setting

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.IconButton
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.Switch
import androidx.compose.material.SwitchColors
import androidx.compose.material.SwitchDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIos
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lulin.wmcamera.ui.widget.NavBar
import com.lulin.wmcamera.ui.widget.SelectMenu
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

/**
 *  @author Lulin
 *  @date 2024/2/27
 *  @desc
 */
@Composable
fun SettingPage(
    viewModel: SettingViewModel = koinViewModel(),
    navBack: () -> Unit
) {

    val uiState by viewModel.uiState.collectAsState(initial = SettingState())
    val scope = rememberCoroutineScope()

    NavBar(
        modifier = Modifier.systemBarsPadding(),
        title = { Text(text = "设置", fontSize = 16.sp) },
        backgroundColor = Color.White,
        navigationIcon = {
            IconButton(
                onClick = { navBack() }
            ) {
                Icon(imageVector = Icons.Default.ArrowBackIos, contentDescription = null, modifier = Modifier.size(24.dp))
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(color = Color(0xFFF3F4F6))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(color = Color.White)
                    .padding(10.dp)
            ) {
                FormItem(label = "姓名", labelPosition = "left") {
                    TransparentTextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = uiState.userName,
                        onValueChange = {
                            viewModel.updateSettingState(
                                uiState.copy(userName = it)
                            )
                        }
                    )
                }

                FormItem(label = "部门", labelPosition = "left") {
                    TransparentTextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = uiState.deptName,
                        onValueChange = {
                            viewModel.updateSettingState(
                                uiState.copy(deptName = it)
                            )
                        }
                    )
                }

                FormItem(label = "开启定位", labelPosition = "left") {
                    Switch(
                        checked = uiState.enableLocation,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color(0xFF2979FF),
                            checkedTrackColor = Color(0xFF2979FF),
                        ),
                        onCheckedChange = {
                            viewModel.updateSettingState(
                                uiState.copy(
                                    enableLocation = it
                                )
                            )
                        }
                    )
                }

                FormItem(label = "水印位置", labelPosition = "left") {
                    SelectMenu(
                        modifier = Modifier.fillMaxWidth(),
                        defaultValue = uiState.waterPosition,
                        keyName = "position",
                        list = positionList,
                        onSelected = {
                            viewModel.updateSettingState(
                                uiState.copy(waterPosition = it)
                            )
                        }
                    )
                }
            }

            CustomButton(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(10.dp),
                text = "保存"
            ) {
                scope.launch {
                    viewModel.saveSetting()
                }
            }
        }

    }
}

@Composable
fun FormItem(
    label: String,
    labelSize: TextUnit = 18.sp,
    labelPosition: String = "top",
    borderBottom: Boolean = false,
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        if (labelPosition == "top") {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp)
            ) {
                Text(text = label, fontSize = labelSize, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 14.dp))
                content()
            }
        } else if (labelPosition == "left") {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp)
            ) {
                Text(text = label, fontSize = labelSize, fontWeight = FontWeight.Bold)
                content()
            }
        }

        if  (borderBottom) {
            Divider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 2.dp)
                    .padding(vertical = 5.dp),
                color = Color(0xFFE4E7ED)
            )
        }

    }

}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun TransparentTextField(
    value: String = "",
    modifier: Modifier,
    placeholder: String = "请输入",
    textAlign: TextAlign = TextAlign.Right,
    singleLine: Boolean = true,
    enabled: Boolean = true,
    maxLength: Int = 100,
    verticalPadding: Dp = 5.dp,
    horizontalPadding: Dp = 10.dp,
    keyboardType: KeyboardType = KeyboardType.Text,
    onValueChange: (String) -> Unit = {}
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusRequester = FocusRequester()
    val focusManager = LocalFocusManager.current

    Box(
        modifier = Modifier
            .then(modifier)
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = { focusManager.clearFocus() }
                )
            }
    ) {
        BasicTextField(
            value = value,
            enabled = enabled,
            modifier = Modifier
                .focusRequester(focusRequester)
                .onFocusChanged {
                    if (!it.isFocused) {
                        keyboardController?.hide()
                    }
                },
            textStyle = LocalTextStyle.current.copy(
                textAlign = textAlign
            ),
            singleLine = singleLine,
            keyboardOptions = KeyboardOptions(
                keyboardType = keyboardType,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    focusManager.clearFocus()
                }
            ),
            decorationBox = { innerTextField ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .background(color = Color.Transparent)
                        .padding(vertical = verticalPadding, horizontal = horizontalPadding),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    if (value.isEmpty()) {
                        Text(
                            text = placeholder,
                            fontSize = 14.sp,
                            color = Color.Gray,
                            modifier = Modifier.padding(vertical = verticalPadding, horizontal = horizontalPadding)
                        )
                    }
                    innerTextField()
                }
            },
            onValueChange = {
                if (it.length <= maxLength) {
                    onValueChange(it)
                }
            }
        )
    }

}

@Composable
fun CustomButton(
    modifier: Modifier,
    text: String,
    onClick: () -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressState = interactionSource.collectIsPressedAsState()
    val buttonColor = Color(if (pressState.value) 0x992B85E4 else 0xFF2979FF)
    Button(
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(
            containerColor = buttonColor,
            disabledContainerColor = Color(0xFFA0CFFF)
        ),
        interactionSource = interactionSource,
        onClick = { onClick.invoke() }
    ) {
        Text(text = text, color = Color.White)
    }
}
