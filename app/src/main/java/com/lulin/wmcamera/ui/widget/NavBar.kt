package com.lulin.wmcamera.ui.widget

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material.ContentAlpha
import androidx.compose.material.IconButton
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIos
import androidx.compose.material.primarySurface
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.layoutId

/**
 *  @author Lulin
 *  @date 2024/1/26
 *  @desc
 */
@SuppressLint("DiscouragedApi")
@Suppress("InternalInsetResource")
@Composable
fun NavBar(
    title: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    navigationIcon: @Composable (() -> Unit)? = null,
    backgroundColor: Color = MaterialTheme.colors.primarySurface,
    actions: @Composable RowScope.() -> Unit = {},
    isImmersive: Boolean = false,
    darkIcons: Boolean = false,
    content: @Composable (PaddingValues) -> Unit,
) {
    val topAppBarHeight = 56.dp
    var statusBarHeight = 0
    var statusBarHeightDp = Dp(0f)
    if (isImmersive) {
//        val systemUiController = rememberSystemUiController()
//        SideEffect {
//            systemUiController.setSystemBarsColor(
//                color = Color.Transparent,
//                darkIcons = darkIcons
//            )
//        }

        with(LocalContext.current) {
            statusBarHeight =
                resources.getDimensionPixelSize(
                    resources.getIdentifier(
                        "status_bar_height",
                    "dimen",
                    "android"
                    )
                )
        }
        with(LocalDensity.current) {
            statusBarHeightDp = statusBarHeight.toDp()
        }
    }

    Scaffold(topBar = {
        val constraintSet = ConstraintSet {
            val titleRef = createRefFor("title")
            val navigationIconRef = createRefFor("navigationIcon")
            val actionsRef = createRefFor("actions")
            constrain(titleRef) {
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom)
            }
            constrain(navigationIconRef) {
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom)
            }
            constrain(actionsRef) {
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom)
                end.linkTo(parent.end)
            }
        }
        ConstraintLayout(constraintSet, modifier = Modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .then(modifier)
            .height(topAppBarHeight + statusBarHeightDp)
            .padding(top = statusBarHeightDp)
        ) {
            Box(
                Modifier
                    .layoutId("title")
                    .padding(horizontal = 4.dp)
            ) {
                ProvideTextStyle(value = MaterialTheme.typography.h6) {
                    CompositionLocalProvider(
                        LocalContentAlpha provides ContentAlpha.high,
                        content = title
                    )
                }
            }
            if (navigationIcon != null) {
                Box(modifier = Modifier
                    .layoutId("navigationIcon")
                    .padding(start = 4.dp)) {
                    CompositionLocalProvider(
                        LocalContentAlpha provides ContentAlpha.high,
                        content = navigationIcon
                    )
                }
            }
            Row(
                Modifier
                    .layoutId("actions")
                    .padding(end = 4.dp),
                content = actions
            )

        }
    }) {
        content(it)
    }

}

@Preview
@Composable
fun NavbarPreview() {
    NavBar(
        modifier = Modifier.systemBarsPadding(),
        backgroundColor = Color.White,
        title = { Text(text = "纸券发行", fontSize = 16.sp) },
        navigationIcon = {
            IconButton(
                onClick = {  }
            ) {
                Icon(imageVector = Icons.Default.ArrowBackIos, contentDescription = null, modifier = Modifier.size(24.dp))
            }
        }
    ) {
        Box(modifier = Modifier.padding(it)) {

        }
    }
}