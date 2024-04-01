package com.lulin.wmcamera.inject

import com.lulin.wmcamera.repository.SettingRepository
import com.lulin.wmcamera.ui.camera.CameraViewModel
import com.lulin.wmcamera.ui.setting.SettingViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

/**
 *  @author Lulin
 *  @date 2024/1/23
 *  @desc
 */

private val viewModelModules = module {
    viewModel {
        CameraViewModel(
            settingRepository = get()
        )
    }
    viewModel {
        SettingViewModel(
            settingRepository = get()
        )
    }
}

private val dataModules = module {
    // SQLite Database
    single {
//        Room.databaseBuilder(
//            androidContext(),
//            AppDatabase::class.java, "app-db"
//        ).build()
    }
    single { SettingRepository() }
}

//private val domainModules = module {
//    single {
//
//    }
//
//    single {
//
//    }
//
//
//}

val AppModules = listOf(viewModelModules, dataModules)