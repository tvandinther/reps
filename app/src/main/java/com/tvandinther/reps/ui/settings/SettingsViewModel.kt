package com.tvandinther.reps.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tvandinther.reps.data.AppSettings
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class SettingsUiState(
    val sessionGapMinutes: Int = AppSettings.DEFAULT_SESSION_GAP_MINUTES,
    val isLeftHanded: Boolean = false,
    val chartSetCount: Int = AppSettings.DEFAULT_CHART_SET_COUNT,
)

class SettingsViewModel(
    private val appSettings: AppSettings,
) : ViewModel() {

    val uiState: StateFlow<SettingsUiState> = combine(
        appSettings.sessionGapMinutes,
        appSettings.isLeftHanded,
        appSettings.chartSetCount,
    ) { gap, leftHanded, chartCount ->
        SettingsUiState(
            sessionGapMinutes = gap,
            isLeftHanded = leftHanded,
            chartSetCount = chartCount,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SettingsUiState())

    fun setSessionGapMinutes(minutes: Int) {
        viewModelScope.launch { appSettings.setSessionGapMinutes(minutes) }
    }

    fun setLeftHanded(enabled: Boolean) {
        viewModelScope.launch { appSettings.setLeftHanded(enabled) }
    }

    fun setChartSetCount(count: Int) {
        viewModelScope.launch { appSettings.setChartSetCount(count) }
    }
}
