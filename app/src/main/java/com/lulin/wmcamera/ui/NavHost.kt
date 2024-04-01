package com.lulin.wmcamera.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.lulin.wmcamera.routes.Routes
import com.lulin.wmcamera.ui.camera.CameraPage
import com.lulin.wmcamera.ui.setting.SettingPage
import com.lulin.wmcamera.utils.Animation
import org.koin.androidx.compose.KoinAndroidContext
import org.koin.core.annotation.KoinExperimentalAPI

/**
 *  @author Lulin
 *  @date 2024/1/23
 *  @desc
 */
@OptIn(KoinExperimentalAPI::class)
@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = Routes.Camera.name
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        enterTransition = { Animation.navigateInAnimation },
        exitTransition = { Animation.navigateUpAnimation },
    ) {
        composable(Routes.Camera.name) {
            CameraPage(
                navSetting = {
                    navController.navigate(Routes.Setting.name)
                }
            )
        }
        composable(Routes.Setting.name) {
            SettingPage(
                navBack = { navController.popBackStack() }
            )
        }
//        composable("${Routes.Search.name}/{query}") {
//            val query = it.arguments?.getString("query") ?: ""
//            SearchPage(
//                query = query,
//                navBack = { navController.popBackStack() },
//                onPhotoClick = { _, index ->
//                    navController.navigate("${Routes.Display.name}/${index}")
//                }
//            )
//        }
//        composable(
//            "${Routes.Display.name}/{index}",
//            arguments = listOf(navArgument("index") { type = NavType.IntType })
//        ) {
//            val initialIndex: Int = it.arguments?.getInt("index") ?: 0
//            DisplayPage(
//                initialPage = initialIndex,
//                onNavigateBack = {
//                    navController.popBackStack()
//                }
//            )
//        }
    }
}