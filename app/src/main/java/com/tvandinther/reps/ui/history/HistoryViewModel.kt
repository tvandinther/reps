package com.tvandinther.reps.ui.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tvandinther.reps.data.AppSettings
import com.tvandinther.reps.data.db.ExerciseDao
import com.tvandinther.reps.data.db.SetDao
import com.tvandinther.reps.data.model.ExerciseEntity
import com.tvandinther.reps.domain.Session
import com.tvandinther.reps.domain.SessionAssembler
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class HistoryUiState(
    val sessions: List<Session> = emptyList(),
    val exercises: Map<Long, ExerciseEntity> = emptyMap(),
)

class HistoryViewModel(
    setDao: SetDao,
    exerciseDao: ExerciseDao,
    appSettings: AppSettings,
) : ViewModel() {

    private val assembler = SessionAssembler()

    val uiState: StateFlow<HistoryUiState> = combine(
        setDao.getAll(),
        exerciseDao.getAll(),
        appSettings.sessionGapMinutes,
    ) { sets, exercises, gapMinutes ->
        HistoryUiState(
            sessions = assembler.assemble(sets, gapMinutes * 60_000L),
            exercises = exercises.associateBy { it.id },
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), HistoryUiState())
}
