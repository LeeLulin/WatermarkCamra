package com.lulin.wmcamera.ui.setting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lulin.wmcamera.repository.SettingRepository
import com.lulin.wmcamera.utils.showToast
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

/**
 *  @author Lulin
 *  @date 2024/2/27
 *  @desc
 */
class SettingViewModel(
    private val settingRepository: SettingRepository
): ViewModel() {

    private val _uiState = MutableStateFlow(SettingState())
    val uiState: SharedFlow<SettingState> = _uiState

    init {
        viewModelScope.launch {
            settingRepository.getSettingParams().collect {
                _uiState.value = it
            }
        }
    }

    fun updateSettingState(state: SettingState) {
        viewModelScope.launch {
            _uiState.emit(state)
        }
    }
    suspend fun saveSetting() {
        settingRepository.saveSettingParams(_uiState.value)
        showToast("保存成功")
    }

}