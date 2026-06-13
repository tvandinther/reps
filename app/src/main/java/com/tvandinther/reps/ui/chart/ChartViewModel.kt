package com.tvandinther.reps.ui.chart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tvandinther.reps.data.AppSettings
import com.tvandinther.reps.data.db.ExerciseDao
import com.tvandinther.reps.data.db.SetDao
import com.tvandinther.reps.data.db.UnitDao
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn

data class ChartDataPoint(
    val volumeValue: Double,
    val resistanceValue: Double?,
)

data class ChartUiState(
    val exerciseName: String = "",
    val volumeUnitLabel: String = "",
    val resistanceUnitLabel: String = "",
    val points: List<ChartDataPoint> = emptyList(),
    val isSingleAxis: Boolean = false,
    val setCount: Int = AppSettings.DEFAULT_CHART_SET_COUNT,
)

class ChartViewModel(
    private val exerciseId: Long,
    private val exerciseDao: ExerciseDao,
    private val setDao: SetDao,
    private val unitDao: UnitDao,
    private val appSettings: AppSettings,
) : ViewModel() {

    @OptIn(ExperimentalCoroutinesApi::class)
    private val recentSets = appSettings.chartSetCount.flatMapLatest { count ->
        setDao.getRecentForExercise(exerciseId, count)
    }

    val uiState: StateFlow<ChartUiState> = combine(
        exerciseDao.getById(exerciseId),
        unitDao.getAll(),
        recentSets,
        appSettings.chartSetCount,
    ) { ex, units, sets, count ->
        ex ?: return@combine ChartUiState()
        val unitMap = units.associateBy { it.id }
        val volumeUnit = unitMap[ex.volumeUnitId]
        val resistanceUnit = unitMap[ex.resistanceUnitId]
        val isSingleAxis = resistanceUnit?.label == "bodyweight" || resistanceUnit?.label == "none"

        // Query returns DESC (newest first); reverse to get oldest-first for gradient indexing
        val points = sets.reversed().map { set ->
            ChartDataPoint(
                volumeValue = set.volumeValue,
                resistanceValue = set.resistanceValue,
            )
        }

        ChartUiState(
            exerciseName = ex.name,
            volumeUnitLabel = volumeUnit?.label ?: "",
            resistanceUnitLabel = resistanceUnit?.label ?: "",
            points = points,
            isSingleAxis = isSingleAxis,
            setCount = count,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ChartUiState())
}
