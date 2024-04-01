package com.lulin.wmcamera.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.lulin.wmcamera.WMApplication
import com.lulin.wmcamera.ui.setting.SettingState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json

/**
 *  @author Lulin
 *  @date 2024/2/27
 *  @desc
 */
class SettingRepository {

    private companion object {
        val CONFIG = stringPreferencesKey("CONFIG")
    }

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "setting")

    fun getSettingParams(): Flow<SettingState> {
        return WMApplication.context.dataStore.data
            .map {  setting ->
                if (setting[CONFIG].isNullOrEmpty()) {
                    SettingState()
                } else {
                    Json.decodeFromString(SettingState.serializer(), setting[CONFIG]!!)
                }
            }
    }

    suspend fun saveSettingParams(settingState: SettingState) {
        WMApplication.context.dataStore.edit { setting ->
            setting[CONFIG] = Json.encodeToString(SettingState.serializer(), settingState)
        }
    }
}